package aho.unamur.demo;

import aho.unamur.fakeHttp.Get;
import aho.unamur.fakeHttp.Server;

public class HelloServer extends Server {

    @Get("/hello")
    public String hello() {
        return "Hello, world";
    }
}
