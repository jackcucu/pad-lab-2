package md.jack.node;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.Configurations;
import md.jack.config.NodeConfig;
import md.jack.dto.DataWrapper;
import md.jack.runners.NodeRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static md.jack.util.Constants.Data.DATA;

@Slf4j
public class Node
{
    public static void main(final String[] args) throws IOException
    {
        final Executor executor = Executors.newFixedThreadPool(50);

        if (args.length < 1)
        {
            log.error("Enter node port as arg");
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

            final NodeConfig nodeConfig = first.orElseThrow(() ->
                    new RuntimeException("Cannot find requested bind port in configuration"));

            final ServerSocket serverSocket = new ServerSocket(nodeConfig.getBindPort());

            final List<NodeConfig> slaves = configurations.getNodeConfigs()
                    .stream()
                    .filter(it -> nodeConfig.getSlaves() != null)
                    .filter(it -> nodeConfig.getSlaves().stream().anyMatch(sl -> it.getName().equalsIgnoreCase(sl)))
                    .collect(Collectors.toList());

            log.info("NodeConfig server started on port {} waiting for requests...", nodeConfig.getBindPort());

            while (true)
            {
                final Socket socket = serverSocket.accept();
                log.info("Received connection from {} on port {}", socket.getInetAddress(), socket.getPort());

                final NodeRunner nodeRunner = NodeRunner.builder()
                        .maven(nodeConfig.isMaster())
                        .slaves(slaves)
                        .socket(socket)
                        .data(new DataWrapper(DATA.get(nodeConfig.getName())))
                        .build();

                executor.execute(nodeRunner);
            }
        }
    }
}
