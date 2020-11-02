package aho.unamur.fakeHttp;

public class Agent {
    public final Response get(Server target, String path, String jwt) {
        return target.receiveGet(path, jwt);
    }
    public final Response get(Server target, String path) {
        return target.receiveGet(path, null);
    }

    public final Response post(Server target, String path, String jwt, String body) {
        return target.receivePost(path, jwt, body);
    }

    public final Response post(Server target, String path, String body) {
        return target.receivePost(path, body);
    }
}
