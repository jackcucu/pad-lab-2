package md.jack.model;

import lombok.Data;

@Data
public class Request
{
    private String query;

    private ContentType contentType;
}
