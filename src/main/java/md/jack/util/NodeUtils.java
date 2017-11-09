package md.jack.util;

import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class NodeUtils
{
    public static String get(final NodeConfig it)
    {
        try
        {
            final Socket socket1 = new Socket(it.getIp(), it.getBindPort());
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            return reader.lines().findFirst().orElse("");
        }
        catch (IOException exception)
        {
            log.error(exception.getMessage());
            return "";
        }
    }
}
