package aho.unamur.demo;

import aho.unamur.fakeHttp.Client;
import aho.unamur.fakeHttp.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsArrayWithSize;
import org.hamcrest.core.StringRegularExpression;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestNewsServer {
    static Client client;
    static LoginServer loginServer;
    static NewsServer newsServer;
    static String bobToken;
    static String aliceToken;

    @BeforeClass
    public static void setupClass() {
        client = new Client();
        newsServer = new NewsServer("very-strong-secret");
        newsServer.publishNews("You'll never believe what happened.");
        newsServer.publishNews("Foot will never be the same !");
        newsServer.publishNews("Top 5 creepiest celebrities, guess who's 1st ?");
        newsServer.publishNews("Your favourite football club will win, here's why...");
        newsServer.publishNews("That guy succeeds everything, learn how !");
        loginServer = new LoginServer("very-strong-secret");
        Response response = loginServer.register("bob", "1234");
        bobToken = response.getHeader("Authorization");
        loginServer.register("alice", "strong-password");

        aliceToken = loginServer.addLicense("alice", "news");
    }

    @Test
    public void testGetLatestNews_unauthorized() {
        Response response = client.get(newsServer, "/latest-article");

        Assert.assertEquals(401, response.getCode());
    }

    @Test
    public void testGetLatestNews_success() {
        Response response = client.get(newsServer, "/latest-article", bobToken);

        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testGetNews_unauthorized() {
        Response response = client.get(newsServer, "/news");

        Assert.assertEquals(401, response.getCode());
    }

    @Test
    public void testGetNews_forbidden() {
        Response response = client.get(newsServer, "/news", bobToken);

        Assert.assertEquals(403, response.getCode());
    }

    @Test
    public void testGetNews_success() {
        Response response = client.get(newsServer, "/news", aliceToken);

        Assert.assertEquals(200, response.getCode());
    }

    @Test
    public void testGetFoot() {
        Response response = client.get(newsServer, "/foot", aliceToken);

        Assert.assertEquals(200, response.getCode());
        String[] footNews = response.getBody().split("\n");
        MatcherAssert.assertThat(footNews, IsArrayWithSize.arrayWithSize(2));
        for (String article : footNews) {
            MatcherAssert.assertThat(article, StringRegularExpression.matchesRegex(".*[Ff]oot.*"));
        }
    }

    @Test
    public void testPublishArticle_forbidden() {
        Response response = client.post(newsServer, "/news", aliceToken, "article=White Rabbit Escaped !");

        Assert.assertEquals(403, response.getCode());
    }

    @Test
    public void testPublishArticle_forgedToken() {
        LoginServer malloryServer = new LoginServer("mallory-secret");
        String forgedToken = malloryServer.login("admin", "admin").getHeader("Authorization");

        Response response = client.post(newsServer, "/news", forgedToken, "article=Evil news to spread fear !");

        Assert.assertEquals(401, response.getCode());
    }

    @Test
    public void testPublishArticle_success() {
        String adminToken = loginServer.login("admin", "admin").getHeader("Authorization");

        Response response = client.post(newsServer, "/news", adminToken, "article=End of the tunnel finally seen !");

        Assert.assertEquals(201, response.getCode());
    }
}
