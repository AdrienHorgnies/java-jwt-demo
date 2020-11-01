package aho.unamur.javaJwtDemo;

import aho.unamur.javaJwtDemo.fakeHttp.HttpServer;
import aho.unamur.javaJwtDemo.fakeHttp.Get;

public class HelloWorldServer extends HttpServer {
    @Get("/hello")
    public String getHelloWorld() {
        return "Hello, world !";
    }
}
