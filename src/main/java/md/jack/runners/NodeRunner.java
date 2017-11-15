package md.jack.runners;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;
import md.jack.dto.DataDto;
import md.jack.dto.DataWrapper;
import md.jack.model.Request;
import md.jack.resolver.QueryResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static md.jack.util.NodeUtils.get;

@Data
@Builder
@Slf4j
public class NodeRunner implements Runnable
{
    private Socket socket;

    private boolean maven;

    private List<NodeConfig> slaves;

    private DataWrapper data;

    @Override
    public void run()
    {
        try
        {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            final Gson gson = new Gson();

            final Request request = gson.fromJson(reader.lines().findFirst().orElse(""), Request.class);

            final QueryResolver queryResolver = new QueryResolver(request.getQuery());

            final List<DataDto> filter = queryResolver.filter(data.getData());

            data.setData(filter);

            if (maven)
            {
                final Stream<DataWrapper> dataWrapperStream = slaves.stream()
                        .map(it -> get(it, request))
                        .map(it -> gson.fromJson(it, DataWrapper.class));

                final List<DataDto> finalData = Stream.concat(dataWrapperStream, Stream.of(data))
                        .filter(Objects::nonNull)
                        .map(DataWrapper::getData)
                        .flatMap(Collection::stream)
                        .collect(toList());


                writer.println(gson.toJson(new DataWrapper(finalData)));

            }
            else
            {
                writer.println(gson.toJson(data));
            }
            socket.close();
        }
        catch (IOException exception)
        {
            log.error(exception.getMessage());
        }
    }
}
