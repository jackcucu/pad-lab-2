package md.jack.runners;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import md.jack.config.NodeConfig;
import md.jack.dto.DataDto;
import md.jack.dto.DataWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Data
@Builder
public class NodeRunner implements Runnable
{
    private Socket socket;

    private boolean maven;

    private List<NodeConfig> slaves;

    private DataWrapper data;

    private static String get(final NodeConfig it)
    {
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
    }

    @Override
    public void run()
    {
        try
        {
            final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            final Gson gson = new Gson();

            if (maven)
            {
                final Stream<DataWrapper> dataWrapperStream = slaves.stream()
                        .map(NodeRunner::get)
                        .map(it -> gson.fromJson(it, DataWrapper.class));

                final List<DataDto> finalData = Stream.concat(dataWrapperStream, Stream.of(data))
                        .map(DataWrapper::getDataDtos)
                        .flatMap(Collection::stream)
                        .collect(toList());

                writer.println(gson.toJson(new DataWrapper(data.getNodeName(), finalData)));

            }
            else
            {
                writer.println(gson.toJson(data));
            }
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
