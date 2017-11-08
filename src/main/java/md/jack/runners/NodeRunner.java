package md.jack.runners;

import lombok.Data;
import md.jack.config.NodeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class NodeRunner implements Runnable
{
    private Socket socket;

    private boolean maven;

    private List<NodeConfig> slaves;

    public NodeRunner(final Socket socket, final boolean maven, final List<NodeConfig> slaves)
    {
        this.socket = socket;
        this.maven = maven;
        this.slaves = slaves;
    }

    @Override
    public void run()
    {
        final PrintWriter writer;
        try
        {
            writer = new PrintWriter(socket.getOutputStream(), true);
            if (maven)
            {
                final Stream<String> nothing = slaves.stream().map(it -> {
                    try
                    {
                        final Socket socket1 = new Socket(it.getIp(), it.getBindPort());
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                        while (true)
                        {
                            return reader.lines().findFirst().orElse("nothing");
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        return "";
                    }
                });

                final String collect = Stream.concat(nothing, Stream.of("master" + socket.getPort()))
                        .collect(Collectors.joining());

                writer.println(collect);

            }
            else
            {
                writer.println("data" + socket.getPort());
            }
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
