# Demonstration of java-jwt

Demonstration of the [java-jwt library](https://github.com/auth0/java-jwt), its features and use cases, in the context
    of the course "Sécurité informatique" IHDCM035 of UNamur.
    
This project mimics modern web servers such as [Spring Web](https://fr.wikipedia.org/wiki/Spring_(framework)) 
and showcases the use of JWT with the specific implementation java-jwt.
Everything happens in memory and thus this project doesn't offer any real world features. 

The demonstration uses a simple yet demonstrative architecture composed of several micro-services.
A micro-service handle login and licenses while the other micro-services provide protected resources.

**The most relevant feature is that the servers can trust legitimate requests 
and discard illegitimate requests on their own; without contacting any other server.**
    
## Demonstration Components

Several components let you play with the concept of JWT.

### Hello Server

The Hello server is very friendly and doesn't require any kind of authentication:

- `GET "/hello"` responds with "Hello, World !"

### Login Server

The Login server enables you to register a user, login, add license to a user or say hello:

- `POST "/register"` asks for a body containing username and password, create the user
and responds with the JWT assigned to the `"Authorization"` header.
- `POST "/login"` asks for a body containing username and password,
and if username exists and password is correct,
responds with the JWT assigned to the `"Authorization"` header.
- `GET "/hello"` responds to authenticated users only.

It also provides a method `addLicense(String username, String license)` which is not exposed by any URL. 

### News Server

The News server only interacts with authenticated users and enables you to read news and publish them too:

- `GET "/latest-article"` responds with the latest article
- `GET "/news"` responds to users with the license "news" with published news. 
- `POST "/news"` asks for the license "admin" and for a body containing an article.
- `GET "/foot"` responds to users with the license "news" with news about foot.

### Time Server

The Time server only interacts with authenticated users owning the license "time" and give the current UTC time:

- `GET "/time"` responds with the current UTC TIME

As an example implementation, see `TimeServer` source code below:

```java
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
```

And here is how it's called:
```java
class Main {
    public static void main(String[] args) {
        Client client = new Client();
        TimeServer timeServer = new TimeServer("strong-server-secret")

        Response response = client.get(timeServer, "/time", token);
        
        int responseCode = response.getCode();
        String time = response.getBody();
    }
}
```

## Create a Custom Server

To easily create a new custom server, the submodule `aho.unamur.fakeHttp` provides you with:
- The `Server` class you must extend with your class.
- The `Get` and `Post` annotations to add above your server methods with the relevant path as a parameter.
It makes the method available to `Client` instances through the corresponding HTTP verb.
- The `Authenticated` annotation to add above your server's methods, or the server class.
It requires call to the endpoint to be authenticated with a JWT signed with the same server secret.
- The `License` annotation to add above your server methods with the relevant license as a parameter.
It requires call to the endpoint to claim the same license with a JWT signed with the same server secret.