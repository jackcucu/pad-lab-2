package md.jack.proxy;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.Configurations;
import md.jack.config.NodeConfig;
import md.jack.runners.ProxyRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static md.jack.util.Constants.Server.BIND_PORT;

@Slf4j
@Data
public class ProxyServer
{
    private Executor executor = Executors.newFixedThreadPool(50);

    private List<NodeConfig> mavens;

    public ProxyServer() throws IOException
    {
        final Configurations configurations = new Gson().fromJson(lines(get("config.json")).collect(joining()),
                Configurations.class);

        this.mavens = configurations.getNodeConfigs().stream().filter(NodeConfig::isMaster).collect(toList());

    }

    public void start() throws IOException
    {
        final ServerSocket serverSocket = new ServerSocket(BIND_PORT);

        log.info("Proxy server started on port {} waiting for clients...", BIND_PORT);

        while (true)
        {
            final Socket socket = serverSocket.accept();

            executor.execute(new ProxyRunner(socket, mavens));
        }
    }

    public static void main(final String[] args) throws IOException
    {
        final ProxyServer proxyServer = new ProxyServer();
        proxyServer.start();
    }

}
