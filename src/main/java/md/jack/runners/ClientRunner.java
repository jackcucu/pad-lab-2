package md.jack.runners;

import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

@Slf4j
public class ClientRunner implements Runnable
{
    private Socket socket;

    private List<NodeConfig> mavens;

    public ClientRunner(final Socket socket, final List<NodeConfig> mavens)
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

            String message;

            while (true)
            {
                while ((message = reader.readLine()) != null)
                {
                    mavens.stream().map(it -> {
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
                    }).forEach(writer::println);
                }
            }
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
    }
}
