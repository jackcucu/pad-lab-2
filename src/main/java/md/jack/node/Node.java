package md.jack.node;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.Configurations;
import md.jack.config.NodeConfig;
import md.jack.runners.NodeRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class Node
{
    public static void main(final String[] args) throws IOException
    {
        final Executor executor = Executors.newFixedThreadPool(50);

        if (args.length < 1)
        {
            System.out.println("Enter node port as arg");
        }
        else
        {
            final Gson gson = new Gson();
            final String json = Files.lines(Paths.get("config.json")).collect(Collectors.joining());
            final Configurations configurations = gson.fromJson(json, Configurations.class);
            final Optional<NodeConfig> first = configurations.getNodeConfigs()
                    .stream()
                    .filter(it -> it.getBindPort() == Integer.parseInt(args[0]))
                    .findFirst();
            final NodeConfig nodeConfig = first.orElseThrow(() -> new RuntimeException("Cannot find requested bind port in configuration"));
            final ServerSocket serverSocket = new ServerSocket(nodeConfig.getBindPort());
            log.info("NodeConfig server started on port {} waiting for requests...", nodeConfig.getBindPort());

            while (true)
            {
                final Socket socket = serverSocket.accept();
                executor.execute(new NodeRunner(socket, nodeConfig.isMaster(), nodeConfig.getSlaves()));
            }
        }
    }
}
