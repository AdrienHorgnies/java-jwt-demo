package aho.unamur.fakeHttp;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private final Map<String, String> headers;
    private final int code;
    private final String body;

    public Response(int code, String body) {
        this.headers = new HashMap<>();
        this.code = code;
        this.body = body;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
