package md.jack.resolver;

import md.jack.dto.DataDto;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class QueryResolver
{
    private List<String> queryList;

    public QueryResolver(final String query)
    {
        this.queryList = Stream.of(query.split(" "))
                .map(String::trim)
                .collect(toList());
    }


    public List<DataDto> filter(List<DataDto> data)
    {
        final List<String> filter = queryList.stream()
                .filter(it -> it.contains("FILTER"))
                .map(it -> it.replace("FILTER(", ""))
                .map(it -> it.replace(")", ""))
                .map(it -> it.split(","))
                .flatMap(Arrays::stream)
                .collect(toList());

        for (String it : filter)
        {
            final String[] split = it.split(":");

            if (split[0].equalsIgnoreCase("id"))
            {
                switch (split[1])
                {
                    case "eq":
                        data = data.stream()
                                .filter(o -> o.getId() == Integer.parseInt(split[2]))
                                .collect(toList());
                        break;

                    case "gt":
                        data = data.stream()
                                .filter(o -> o.getId() > Integer.parseInt(split[2]))
                                .collect(toList());
                        break;

                    case "lt":
                        data = data.stream()
                                .filter(o -> o.getId() < Integer.parseInt(split[2]))
                                .collect(toList());
                        break;
                }
            }
            else if (split[0].equalsIgnoreCase("text"))
            {
                switch (split[1])
                {
                    case "eq":
                        data = data.stream()
                                .filter(o -> o.getText().equalsIgnoreCase(split[2]))
                                .collect(toList());
                        break;

                    case "contains":
                        data = data.stream()
                                .filter(o -> o.getText().contains(split[2]))
                                .collect(toList());
                        break;
                }
            }
        }
        return data;
    }

    public void sort(final List<DataDto> data)
    {
        final List<String> sort = queryList.stream()
                .filter(it -> it.contains("SORT"))
                .map(it -> it.replace("SORT(", ""))
                .map(it -> it.replace(")", ""))
                .map(it -> it.split(","))
                .flatMap(Arrays::stream)
                .collect(toList());

        sort.forEach(it -> compare(data, it));
    }

    private void compare(final List<DataDto> data, final String it)
    {
        if (it.contains("id"))
        {
            Comparator<DataDto> comparing = comparing(DataDto::getId);

            if (it.contains("-"))
            {
                comparing = comparing.reversed();
            }

            data.sort(comparing);
        }
        else if (it.contains("text"))
        {
            Comparator<DataDto> comparing = comparing(DataDto::getText);

            if (it.contains("-"))
            {
                comparing = comparing.reversed();
            }
            data.sort(comparing);
        }
    }
}
