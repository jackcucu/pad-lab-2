package md.jack.client;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import md.jack.dto.DataWrapper;
import md.jack.model.ContentType;
import md.jack.model.Request;
import md.jack.validators.ValidationUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Scanner;

import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.joining;
import static md.jack.model.ContentType.JSON;
import static md.jack.model.ContentType.XML;
import static md.jack.util.Constants.Server.BIND_PORT;

@Slf4j
public class Client
{
    public static void main(final String[] args)
    {
        if (args.length < 1)
        {
            log.error("Enter client content-type as arg(xml or json)");
        }
        else
        {
            if (args[0].equalsIgnoreCase("xml") || args[0].equalsIgnoreCase("json"))
            {
                try
                {
                    final Socket socket = new Socket("localhost", BIND_PORT);

                    if (socket.isConnected())
                    {
                        log.info("Client connected to {} on port {}",
                                socket.getInetAddress(),
                                socket.getPort());

                        final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        final Unmarshaller unmarshaller = getUnmarshaller();

                        final Gson gson = new Gson();

                        final Request request = new Request();

                        request.setContentType(args[0].equalsIgnoreCase("xml") ? XML : JSON);

                        System.out.println("Usage SORT(field1, -field2)\"-{desc}\" FILTER(field:operator:value) \"operator:gt, lt, eq, contains");
                        while (true)
                        {
                            System.out.println("Query : ");

                            request.setQuery(new Scanner(System.in).nextLine());

                            writer.println(gson.toJson(request));

                            final DataWrapper dataWrapper = reader.lines()
                                    .findFirst()
                                    .map(it -> unmarshall(unmarshaller, gson, request.getContentType(), it))
                                    .orElse(null);

                            dataWrapper.getData().forEach(it -> {
                                System.out.println(it.getId() + " " + it.getText());
                            });
                        }
                    }
                }
                catch (Exception exception)
                {
                    log.error("Error {} with cause {}", exception.getMessage(), exception.getCause());
                }
            }
        }
    }

    private static Unmarshaller getUnmarshaller() throws JAXBException, SAXException
    {
        final JAXBContext context = JAXBContext.newInstance(DataWrapper.class);

        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = sf.newSchema(new File("xml_schema.xsd"));

        final Unmarshaller unmarshaller = context.createUnmarshaller();

        unmarshaller.setSchema(schema);

        unmarshaller.setEventHandler(Client::handleEvent);
        return unmarshaller;
    }

    private static DataWrapper unmarshall(final Unmarshaller unmarshaller,
                                          final Gson gson,
                                          final ContentType contentType,
                                          final String it)
    {
        try
        {
            log.info(it);
            if (contentType.equals(XML))
            {
                final XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance()
                        .createXMLStreamReader(new StringReader(it));

                return (DataWrapper) unmarshaller.unmarshal(xmlStreamReader);
            }
            else
            {
                if (ValidationUtils.isJsonValid(lines(get("json-schema.json")).collect(joining()), it))
                {
                    log.info("Valid json");
                }
                else
                {
                    log.error("Invalid json");
                }

                return gson.fromJson(it, DataWrapper.class);
            }
        }
        catch (Exception exception)
        {
            log.error(exception.getMessage());
            return null;
        }
    }

    private static boolean handleEvent(final ValidationEvent event)
    {
        log.error(event.getLinkedException().toString());
        return true;
    }
}
