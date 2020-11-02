package aho.unamur.demo;

import aho.unamur.fakeHttp.Client;
import aho.unamur.fakeHttp.Response;
import org.junit.Assert;
import org.junit.Test;

public class TestTimeServer {
    static Client client = new Client();
    static TimeServer timeServer = new TimeServer("strong-secret");

    @Test
    public void testTime_forbidden() {
        LoginServer loginServer = new LoginServer("strong-secret");
        loginServer.register("bob", "1234");
        String token = loginServer.addLicense("bob", "news");

        Response response = client.get(timeServer, "/time", token);

        Assert.assertEquals(403, response.getCode());
    }

    @Test
    public void testTime_success() {
        LoginServer loginServer = new LoginServer("strong-secret");
        loginServer.register("alice", "1234");
        loginServer.addLicense("alice", "news");
        String token = loginServer.addLicense("alice", "time");

        Response response = client.get(timeServer, "/time", token);

        Assert.assertEquals(200, response.getCode());
    }
}
