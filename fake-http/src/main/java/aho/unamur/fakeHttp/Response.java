package aho.unamur.fakeHttp;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private final Map<String, String> headers;
    private final int code;
    private final String body;

    public Response(int code, String body, String headers) {
        this.headers = new HashMap<>();
        this.code = code;
        this.body = body;

        if (headers.isEmpty()) {
            return;
        }
        for (String pair : headers.split("&")) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue[1];
            this.headers.put(key, value);
        }
    }

    public Response(int code, String body) {
        this(code, body, "");
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
