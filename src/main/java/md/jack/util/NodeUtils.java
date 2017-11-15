package md.jack.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.config.NodeConfig;
import md.jack.model.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class NodeUtils
{
    public static String get(final NodeConfig it, final Request request)
    {
        try
        {
            final Socket socket = new Socket(it.getIp(), it.getBindPort());

            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println(new Gson().toJson(request));

            final String s = reader.lines().findFirst().orElse("");
            return s;
        }
        catch (IOException exception)
        {
            log.error(exception.getMessage());
            return "";
        }
    }
}
