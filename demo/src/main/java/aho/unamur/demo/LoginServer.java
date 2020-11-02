package aho.unamur.demo;

import aho.unamur.fakeHttp.*;
import com.auth0.jwt.JWT;

import java.util.HashMap;
import java.util.Map;

public class LoginServer extends Server {
    // never store clear-text password ! I can do it here because everything is fake !
    public Map<String, String> pwByUsername;

    public LoginServer(String secret) {
        super(secret);
        this.pwByUsername = new HashMap<>();
    }

    @Post("/register")
    public Response register(String username, String password) {
        pwByUsername.put(username, password);

        String jwt = createJwt(username);

        return new Response(201, "User Created", "Authorization="+jwt);
    }

    @Post("/login")
    public Response login(String username, String password) {
        if (!pwByUsername.containsKey(username)) {
            return new Response(404, "User not found");
        }
        if (!pwByUsername.get(username).equals(password)) {
            return new Response(401, "Unauthorized");
        }

        String jwt = createJwt(username);

        return new Response(201, "JWT Created", "Authorization=" + jwt);
    }

    @Get("/hello")
    @Authenticated
    public Response hello() {
        return new Response(200, "Hello my dear authenticated user !");
    }

    private String createJwt(String username) {
        return JWT.create()
                .withIssuer("aho.unamur")
                .withClaim("username", username)
                .sign(this.algorithm);
    }
}
