package md.jack.runners;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;
import md.jack.dto.DataDto;
import md.jack.dto.DataWrapper;
import md.jack.model.Request;
import md.jack.resolver.QueryResolver;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static md.jack.model.ContentType.XML;
import static md.jack.util.NodeUtils.get;

@Slf4j
public class ProxyRunner implements Runnable
{
    private Socket socket;

    private List<NodeConfig> mavens;

    public ProxyRunner(final Socket socket, final List<NodeConfig> mavens)
    {
        this.socket = socket;
        this.mavens = mavens;
    }

    @Override
    public void run()
    {
        try
        {
            log.info("Connection with client from {} on port {}", socket.getInetAddress(), socket.getPort());

            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);

            final Gson gson = new Gson();

            String message;

            final Marshaller jaxbMarshaller = JAXBContext.newInstance(
                    DataWrapper.class)
                    .createMarshaller();

            while (true)
            {
                while ((message = reader.readLine()) != null)
                {
                    final Request request = gson.fromJson(message, Request.class);

                    final QueryResolver queryResolver = new QueryResolver(request.getQuery());

                    final List<DataDto> data = mavens.stream()
                            .map(it -> get(it, request))
                            .map(it -> gson.fromJson(it, DataWrapper.class))
                            .map(DataWrapper::getData)
                            .flatMap(Collection::stream)
                            .collect(toList());

                    queryResolver.sort(data);

                    final DataWrapper dataWrapper = new DataWrapper(data);

                    if (request.getContentType().equals(XML))
                    {
                        final StringWriter stringWriter = new StringWriter();

                        jaxbMarshaller.marshal(dataWrapper, stringWriter);


                        writer.println(stringWriter.toString());
                    }
                    else
                    {
                        final String s = gson.toJson(dataWrapper);
                        writer.println(s);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
    }
}
