package aho.unamur.javaJwtDemo.fakeHttp;

public class HttpAgent {
    public final Response get(HttpServer target, String path, String jwt) {
        return target.receiveGet(path, jwt);
    }
    public final Response get(HttpServer target, String path) {
        return target.receiveGet(path, null);
    }

    public final Response post(HttpServer target, String path, String jwt, String body) {
        return target.receivePost(path, jwt, body);
    }

    public final Response post(HttpServer target, String path, String body) {
        return target.receivePost(path, body);
    }
}
