package aho.unamur.demo;

import aho.unamur.fakeHttp.Client;
import aho.unamur.fakeHttp.Response;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Assert;
import org.junit.Test;

public class TestLoginServer {
    private static String SECRET = "123456789";

    @Test
    public void testLogin_success() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);
        loginServer.register("bob", "1234");

        Response response = client.post(loginServer, "/login", "username=bob&password=1234");

        String authorization = response.getHeader("Authorization");
        DecodedJWT jwt = JWT.decode(authorization);

        Assert.assertEquals(201, response.getCode());
        Assert.assertEquals("bob", jwt.getClaim("username").asString());
        Assert.assertEquals("JWT", jwt.getType());
        Assert.assertEquals("HS256", jwt.getAlgorithm());
    }

    @Test
    public void testLogin_notFound() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);

        Response response = client.post(loginServer, "/login", "username=bob&password=1234");

        Assert.assertEquals(404, response.getCode());
    }

    @Test
    public void testLogin_badPassword() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);
        loginServer.register("bob", "1234");

        Response response = client.post(loginServer, "/login", "username=bob&password=1111");

        Assert.assertEquals(401, response.getCode());
    }

    @Test
    public void testRegister() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);

        Response response = client.post(loginServer, "/register", "username=bob&password=1111");

        String authorization = response.getHeader("Authorization");
        DecodedJWT jwt = JWT.decode(authorization);

        Assert.assertEquals(201, response.getCode());
        Assert.assertEquals("bob", jwt.getClaim("username").asString());
        Assert.assertEquals("JWT", jwt.getType());
        Assert.assertEquals("HS256", jwt.getAlgorithm());
    }

    @Test
    public void testHello_unauthenticated() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);

        Response response = client.get(loginServer, "/hello");

        Assert.assertEquals(401, response.getCode());
    }

    @Test
    public void testHello_malformedJwt() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);
        loginServer.register("bob", "1234");

        Response response = client.get(loginServer, "/hello", "not-a-jwt");

        Assert.assertEquals(401, response.getCode());
    }

    @Test
    public void testHello_badSignature() {
        Client client = new Client();

        LoginServer loginServer = new LoginServer(SECRET);

        String token = JWT.create()
                .withClaim("username", "bob")
                .sign(Algorithm.HMAC256("not-the-server-secret"));

        Response response = client.get(loginServer, "/hello", token);

        Assert.assertEquals(401, response.getCode());
    }
}
