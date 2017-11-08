package md.jack.client;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class Client
{
    public static void main(final String[] args)
    {
        try
        {
            final Socket socket = new Socket("localhost", 8787);

            if (socket.isConnected())
            {
                log.info("Client connected to {} on port {}",
                        socket.getInetAddress(),
                        socket.getPort());

                final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true)
                {
                    System.out.println("Query : ");
                    final Scanner scanner = new Scanner(System.in);
                    writer.println(scanner.nextLine());

                    reader.lines().findFirst().ifPresent(System.out::println);
                }
            }
        }
        catch (Exception exception)
        {
            log.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
        }
    }
}
