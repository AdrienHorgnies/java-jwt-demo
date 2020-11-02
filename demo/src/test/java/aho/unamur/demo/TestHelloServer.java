package aho.unamur.demo;

import aho.unamur.fakeHttp.Client;
import aho.unamur.fakeHttp.Response;
import org.junit.Assert;
import org.junit.Test;

public class TestHelloServer {
    @Test
    public void testHello() {
        Client client = new Client();

        HelloServer helloServer = new HelloServer();

        Response response = client.get(helloServer, "/hello");

        Assert.assertEquals(200, response.getCode());
        Assert.assertEquals("Hello, World !", response.getBody());
    }
}
