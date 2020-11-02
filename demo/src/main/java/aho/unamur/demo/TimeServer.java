package aho.unamur.demo;

import aho.unamur.fakeHttp.Get;
import aho.unamur.fakeHttp.License;
import aho.unamur.fakeHttp.Response;
import aho.unamur.fakeHttp.Server;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServer extends Server {

    public TimeServer(String secret) {
        super(secret);
    }

    @License("time")
    @Get("/time")
    public Response time() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return new Response(200, timeFormat.format(date));
    }
}
