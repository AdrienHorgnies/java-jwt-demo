package aho.unamur.demo;

import aho.unamur.fakeHttp.Get;
import aho.unamur.fakeHttp.Response;
import aho.unamur.fakeHttp.Server;

public class HelloServer extends Server {

    @Get("/hello")
    public Response hello() {
        return new Response(200, "Hello, World !");
    }
}
