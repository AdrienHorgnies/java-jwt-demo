package aho.unamur.fakeHttp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Server extends Agent {
    private static final Map<String, Method> methodMap = new HashMap<>();

    protected final Response receive(String verb, String path, String jwt, String body) {
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

    protected final Response receiveGet(String path, String jwt) {
        return receive("GET", path, jwt, null);
    }

    protected final Response receiveGet(String path) {
        return receive("GET", path, null, null);
    }

    protected final Response receivePost(String path, String jwt, String body) {
        return receive("POST", path, jwt, body);
    }

    protected final Response receivePost(String path, String body) {
        return receive("POST", path, null, body);
    }
}
