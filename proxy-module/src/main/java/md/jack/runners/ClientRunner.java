package md.jack.runners;

import md.jack.config.Node;

import java.net.Socket;
import java.util.List;

public class ClientRunner implements Runnable
{
    private Socket socket;

    private List<Node> mavens;

    public ClientRunner(final Socket socket, final List<Node> mavens)
    {
        this.socket = socket;
        this.mavens = mavens;
    }

    @Override
    public void run()
    {

    }
}
