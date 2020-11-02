package aho.unamur.fakeHttp;

import org.reflections.Reflections;

import static org.reflections.ReflectionUtils.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Server extends Agent {
    private static final Map<String, Method> methodMap = new HashMap<>();

    public static void addRoute(String verb, String path, Method method) {
        methodMap.put(verb + " - " + path, method);
    }

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
                String responseBody = (String) method.invoke(this);
                return new Response(200, responseBody);
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

    static {
        Reflections reflections = new Reflections("aho.unamur");

        Set<Class<? extends Server>> classes = reflections.getSubTypesOf(Server.class);

        for (Class<? extends Server> serverClass : classes) {
            try {
                Method addRoute = serverClass.getMethod("addRoute", String.class, String.class, Method.class);
                Set<Method> annotatedMethods = getAllMethods(serverClass, withAnnotation(Get.class));
                for (Method method : annotatedMethods) {
                    Get annotation = method.getAnnotation(Get.class);
                    String path = annotation.value();

                    addRoute("GET", path, method);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
