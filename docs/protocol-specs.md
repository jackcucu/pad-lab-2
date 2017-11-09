## Description of the `proxy` protocol

The protocol for the system `proxy` is based on JSON.

`query` is a dsl query for data. It must be compose of the following:

* **Operations** : SORT(-field, field2) "-" is desc , FILTER(field:operator:value, field2:operator:value)
* **Fields** : id, text
* **Operators** : eq(equals), gt(>), lt(<), contains(if string contains in data)

Examples :
```
SORT(-id, text) FILTER(text:contains:INFO)
SORT(id)
FILTER(text:eq:[INFO] 1 data success)
```
`content_tyoe` is content type of response. It must be one of the following:
- `JSON` - json data;
- `XML` - xml data;

Examples of the structure for the `brk` command messages (dicts dumped to json):

```json
{
    "query": "SORT(id) FILTER(text:contains:INFO)",
    "content_type": "JSON"
}
```

```json
{
    "query": "SORT(-id)",
    "content_type": "XML"
}
```

```json
{
    "query": "SORT(id) FILTER(id:eq:1)",
    "content_type": "JSON"
}
```

```json
{
    "query": "SORT(-id) FILTER(id:qt:5)",
    "content_type": "XML"
}
```
