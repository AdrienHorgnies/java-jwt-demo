package aho.unamur.javaJwtDemo;

public class Demo {
    public static void main(String[] args) {
        Client client = new Client();

        HelloWorldServer hello = new HelloWorldServer();

        System.out.println(client.get(hello, "/hello", null));
    }
}
