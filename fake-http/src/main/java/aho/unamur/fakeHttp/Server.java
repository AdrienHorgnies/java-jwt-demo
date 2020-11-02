package aho.unamur.fakeHttp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.reflections.Reflections;

import com.auth0.jwt.algorithms.Algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public abstract class Server extends Agent {
    private final Map<String, Method> methodMap = new HashMap<>();

    public void addRoute(String verb, String path, Method method) {
        methodMap.put(verb + " - " + path, method);
    }

    protected final Algorithm algorithm;
    private final JWTVerifier verifier;

    public Server(String secret) {
        if (secret.isEmpty()) {
            this.algorithm = null;
            this.verifier = null;
        } else {
            this.algorithm = Algorithm.HMAC256(secret);
            this.verifier = JWT.require(this.algorithm).withIssuer("aho.unamur").build();
        }

        Set<Method> getMethods = getAllMethods(this.getClass(), withAnnotation(Get.class));

        for (Method method : getMethods) {
            Get annotation = method.getAnnotation(Get.class);
            String path = annotation.value();

            addRoute("GET", path, method);
        }

        Set<Method> postMethods = getAllMethods(this.getClass(), withAnnotation(Post.class));

        for (Method method : postMethods) {
            Post annotation = method.getAnnotation(Post.class);
            String path = annotation.value();

            addRoute("POST", path, method);
        }
    }

    public Server() {
        this("");
    }

    protected final Response receive(String verb, String path, String jwt, String body) {
        String key = verb + " - " + path;

        if (!methodMap.containsKey(key)) {
            return new Response(404, "Path does not exist");
        }

        Method method = methodMap.get(key);

        if (method.isAnnotationPresent(Authenticated.class)) {
            if (jwt == null) {
                return new Response(401, "Authorization required and not provided");
            }
            try {
                DecodedJWT verify = verifier.verify(jwt);
            } catch (JWTDecodeException | SignatureVerificationException e) {
                return new Response(401, "Authorization failed");
            }

        }

        if (verb.equals("GET") && body != null) {
            return new Response(400, "Get resource does not accept body");
        }

        try {
            if (verb.equals("GET")) {
                return (Response) method.invoke(this);
            }

            if (verb.equals("POST")) {
                List<String> actualParameters = new ArrayList<>();

                // TODO I should set order according to parameter names but that asks more development as
                //  this information is not retained at compilation and it isn't worth it for this demo
                for (String chunk : body.split("&")) {
                    String value = chunk.split("=", 2)[1];
                    actualParameters.add(value);
                }

                return (Response) method.invoke(this, actualParameters.toArray());
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
