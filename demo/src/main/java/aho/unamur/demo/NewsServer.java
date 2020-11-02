package aho.unamur.demo;

import aho.unamur.fakeHttp.*;

import java.util.Stack;
import java.util.stream.Collectors;

@Authenticated
public class NewsServer extends Server {
    private final Stack<String> news = new Stack<>();

    public NewsServer(String secret) {
        super(secret);
        news.push("Very first news !");
    }

    @Get("/latest-article")
    public Response latestArticle() {
        return new Response(200, news.peek());
    }

    @Get("/news")
    @License("news")
    public Response news() {
        return new Response(200, String.join("\n", news));
    }

    @Get("/foot")
    @License("news")
    public Response foot() {
        String footNews = news.stream()
                .filter(s -> s.matches(".*[Ff]oot.*"))
                .collect(Collectors.joining("\n"));
        return new Response(200, footNews);
    }

    @Post("/news")
    @License("admin")
    public Response publishNews(String article) {
        this.news.push(article);
        return new Response(201, "Article published");
    }
}
