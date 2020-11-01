package aho.unamur.javaJwtDemo.fakeHttp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpServer extends HttpAgent {
    private final Map<String, Method> methodMap;

    public HttpServer() {
        super();
        this.methodMap = new HashMap<>();
    }

    protected Response receive(String verb, String path, String jwt, String body) {
        String key = verb + " - " + path;

        if (!methodMap.containsKey(key)) {
            return new Response(404, "Not Found");
        }

        // TODO test jwt if required !

        if (verb.equals("GET") && body != null) {
            return new Response(400, "Get resource does not accept body");
        }

        Method method = methodMap.get(key);

        try {
            if (verb.equals("GET")) {
                return (Response) method.invoke(this);
            }

            return (Response) method.invoke(this, body);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Bad developer should have caught that before catch...");
            e.printStackTrace();
        }

        return new Response(500, "Server Error");
    }

    protected Response receiveGet(String path, String jwt) {
        return receive("GET", path, jwt, null);
    }

    protected Response receiveGet(String path) {
        return receive("GET", path, null, null);
    }

    protected Response receivePost(String path, String jwt, String body) {
        return receive("POST", path, jwt, body);
    }

    protected Response receivePost(String path, String body) {
        return receive("POST", path, null, body);
    }
}
