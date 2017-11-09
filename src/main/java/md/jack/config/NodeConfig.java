package md.jack.config;

import lombok.Builder;
import lombok.Data;

import java.util.List;

public @Builder @Data class NodeConfig
{
    private String name;

    private String ip;

    private int bindPort;

    private boolean master;

    private List<String> slaves;
}
