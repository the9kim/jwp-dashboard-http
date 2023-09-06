package org.apache.coyote.http11.util;

import java.util.HashMap;
import java.util.Map;

public class QueryParamsParser {

    private static final int PARAM_INFO_INDEX = 0;
    private static final int PARAM_VALUE_INDEX = 1;

    public static HashMap<String, String> parseByBody(final String requestBody) {
        try {
            final HashMap<String, String> data = new HashMap<>();
            final String[] params = requestBody.split("&");
            initData(data, params);
            return data;
        } catch (final ArrayIndexOutOfBoundsException e) {
            return new HashMap<>();
        }
    }

    private static void initData(final Map<String, String> data, final String[] params) {
        for (final String param : params) {
            final String paramInfo = param.split("=")[PARAM_INFO_INDEX];
            final String paramValue = param.split("=")[PARAM_VALUE_INDEX];
            data.put(paramInfo, paramValue);
        }
    }
}
