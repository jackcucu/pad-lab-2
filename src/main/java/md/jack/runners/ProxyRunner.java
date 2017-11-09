package md.jack.runners;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;
import md.jack.dto.DataWrapper;
import md.jack.dto.MavenDataWrapper;
import md.jack.model.Request;
import md.jack.resolver.QueryResolver;
import md.jack.util.NodeUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static md.jack.model.ContentType.XML;

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
                    MavenDataWrapper.class)
                    .createMarshaller();

            while (true)
            {
                while ((message = reader.readLine()) != null)
                {
                    final Request request = gson.fromJson(message, Request.class);

                    final QueryResolver queryResolver = new QueryResolver();

                    final List<DataWrapper> collect = mavens.stream()
                            .map(NodeUtils::get)
                            .map(it -> gson.fromJson(it, DataWrapper.class))
                            .peek(it -> it.setData(queryResolver.resolve(request.getQuery(), it.getData())))
                            .collect(toList());

                    final MavenDataWrapper mavenDataWrapper = new MavenDataWrapper(collect);

                    if (request.getContentType().equals(XML))
                    {
                        final StringWriter stringWriter = new StringWriter();

                        jaxbMarshaller.marshal(mavenDataWrapper, stringWriter);


                        writer.println(stringWriter.toString());
                    }
                    else
                    {
                        final String s = gson.toJson(mavenDataWrapper);
                        System.out.println("sdfsfd");
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
