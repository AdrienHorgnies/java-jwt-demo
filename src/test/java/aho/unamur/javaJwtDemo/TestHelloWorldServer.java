package aho.unamur.javaJwtDemo;

import aho.unamur.javaJwtDemo.fakeHttp.Response;
import org.junit.Assert;
import org.junit.Test;

public class TestHelloWorldServer {
    @Test
    public void testHello() {
        Client client = new Client();

        HelloWorldServer hello = new HelloWorldServer();

        Response response = client.get(hello, "/hello");

        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals("Hello, World !", response.getBody());
    }
}
