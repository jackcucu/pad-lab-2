package md.jack.config;

import lombok.Builder;
import lombok.Data;

import java.util.List;
public @Builder @Data class Node
{
    private String name;

    private String ip;

    private String bindPort;

    private boolean master;

    private List<Node> slaves;
}
