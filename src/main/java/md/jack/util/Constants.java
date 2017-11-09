package md.jack.util;

import md.jack.dto.DataDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public final class Constants
{
    public static class Server
    {
        public static final int BIND_PORT = 8787;
    }

    public static class Data
    {
        public static final Map<String, List<DataDto>> DATA = new HashMap<String, List<DataDto>>()
        {
            {
                put("n1", asList(
                        new DataDto(1, "[INFO] operation 1 success"),
                        new DataDto(2, "[INFO] operation 2 success"),
                        new DataDto(3, "[ERROR] operation 3 error"),
                        new DataDto(4, "[INFO] operation 4 success"),
                        new DataDto(5, "[ERROR] operation 5 error")));
                put("n2", asList(
                        new DataDto(6, "[INFO] operation 6 success"),
                        new DataDto(7, "[INFO] operation 7 success"),
                        new DataDto(8, "[ERROR] operation 8 error"),
                        new DataDto(9, "[INFO] operation 9 success"),
                        new DataDto(10, "[INFO] operation 10 success")));
                put("n3", asList(
                        new DataDto(11, "[INFO] operation 11 success"),
                        new DataDto(12, "[ERROR] operation 12 error"),
                        new DataDto(13, "[INFO] operation 13 success")));
                put("n4", asList(
                        new DataDto(14, "[INFO] operation 14 success"),
                        new DataDto(15, "[INFO] operation 15 success"),
                        new DataDto(16, "[INFO] operation 16 success")));
                put("n5", asList(
                        new DataDto(17, "[ERROR] operation 17 error"),
                        new DataDto(18, "[ERROR] operation 18 error"),
                        new DataDto(19, "[INFO] operation 19 success"),
                        new DataDto(20, "[INFO] operation 20 success"),
                        new DataDto(21, "[ERROR] operation 21 error"),
                        new DataDto(22, "[INFO] operation 22 success"),
                        new DataDto(23, "[INFO] operation 23 success")));
                put("n6", asList(
                        new DataDto(24, "[ERROR] operation 24 error"),
                        new DataDto(25, "[INFO] operation 25 success"),
                        new DataDto(26, "[INFO] operation 26 success"),
                        new DataDto(27, "[ERROR] operation 27 error"),
                        new DataDto(28, "[INFO] operation 28 success"),
                        new DataDto(29, "[ERROR] operation 29 error"),
                        new DataDto(30, "[INFO] operation 30 success")));

            }
        };
    }
}
