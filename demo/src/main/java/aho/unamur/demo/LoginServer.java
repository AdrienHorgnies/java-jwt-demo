package aho.unamur.demo;

import aho.unamur.fakeHttp.*;
import com.auth0.jwt.JWT;

import java.time.Instant;
import java.util.*;

public class LoginServer extends Server {
    // never store clear-text password ! I can do it here because everything is fake !
    public Map<String, String> pwByUsername;
    public Map<String, List<String>> licensesByUsername;

    public LoginServer(String secret) {
        super(secret);
        this.pwByUsername = new HashMap<>();
        this.licensesByUsername = new HashMap<>();
        this.pwByUsername.put("admin", "admin");
        List<String> adminLicenses = new ArrayList<>();
        adminLicenses.add("admin");
        this.licensesByUsername.put("admin", adminLicenses);
    }

    @Post("/register")
    public Response register(String username, String password) {
        pwByUsername.put(username, password);
        List<String> licenses = new ArrayList<>();
        licensesByUsername.put(username, licenses);

        String jwt = createJwt(username, licenses);

        return new Response(201, "User Created", "Authorization=" + jwt);
    }

    @Post("/login")
    public Response login(String username, String password) {
        if (!pwByUsername.containsKey(username)) {
            return new Response(404, "User not found");
        }
        if (!pwByUsername.get(username).equals(password)) {
            return new Response(401, "Unauthorized");
        }

        String jwt = createJwt(username, licensesByUsername.get(username));

        return new Response(201, "JWT Created", "Authorization=" + jwt);
    }

    @Get("/hello")
    @Authenticated
    public Response hello() {
        return new Response(200, "Hello my dear authenticated user !");
    }

    public String addLicense(String username, String license) {
        if (!pwByUsername.containsKey(username)) {
            throw new IllegalArgumentException("User does not exist");
        }
        List<String> licenses = licensesByUsername.get(username);

        if (licenses.contains(license)) {
            throw new IllegalArgumentException("User already has that license");
        } else {
            licenses.add(license);
        }

        return createJwt(username, licenses);
    }

    private String createJwt(String username, List<String> licenses) {
        Date now = Date.from(Instant.now());
        return JWT.create()
                .withIssuer("aho.unamur")
                .withSubject(username)
                .withAudience("students", "teachers")
                .withExpiresAt(Date.from(Instant.parse("2020-11-07T13:00:00.000Z")))
                .withNotBefore(now)
                .withIssuedAt(now)
                .withJWTId(username + "-" + now.toInstant().getEpochSecond())
                .withClaim("username", username)
                .withClaim("licenses", licenses)
                .sign(this.algorithm);
    }
}
