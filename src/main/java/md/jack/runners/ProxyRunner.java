package md.jack.runners;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;
import md.jack.dto.DataWrapper;
import md.jack.dto.MavenDataWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

    private static String get(NodeConfig it)
    {
        try
        {
            final Socket socket = new Socket(it.getIp(), it.getBindPort());
            final BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true)
            {
                return r.lines().findFirst().orElse("nothing");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
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

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            while (true)
            {
                while ((message = reader.readLine()) != null)
                {
                    final List<DataWrapper> collect = mavens.stream()
                            .map(ProxyRunner::get)
                            .map(it -> gson.fromJson(it, DataWrapper.class))
                            .collect(toList());

                    final MavenDataWrapper mavenDataWrapper = new MavenDataWrapper(collect);


                    jaxbMarshaller.marshal(mavenDataWrapper, new File("output.xml"));
                    writer.println(gson.toJson(mavenDataWrapper));
                }
            }
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
    }
}
