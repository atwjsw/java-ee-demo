package org.atwjsw.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("hello")
public class HelloWorldRest {

    @Path("{name}")
    @GET
    public JsonObject greet(@PathParam("name") String name) {
        return Json.createObjectBuilder()
            .add("name", name)
            .add("greeting", "Hello, " + name)
            .add("message", "Hello from Jakarta EE!").build();
    }
}
