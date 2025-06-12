package br.mangabaka.api.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/example")
public class ExampleResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExample() {
        var data = new ExampleData("Hello", "World");
        return Response.ok(data).build();
    }
}

record ExampleData(String key, String value) {
}