package org.atwjsw.rest;

import org.atwjsw.config.SecureAuth;
import org.atwjsw.entity.Todo;
import org.atwjsw.entity.TodoUser;
import org.atwjsw.service.PersistenceService;
import org.atwjsw.service.QueryService;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;

@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecureAuth
public class TodoRest {

    @Inject
    PersistenceService persistenceService;

    @Inject
    QueryService queryService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Path("create")
    public Response createTodo(Todo todo) {

        persistenceService.saveTodo(todo);

        //Return a link to newly created Todo
        // return a link to a list of Todos
        URI todoByIdPath = uriInfo.getBaseUriBuilder().path(TodoRest.class).path(todo.getId().toString()).build();//todo/2
        URI listPath = uriInfo.getBaseUriBuilder().path(TodoRest.class).path(TodoRest.class, "listTodo").build();


        // using JSON-P api to construct the response. // {"_links": {{"_self" : "...", "_others": "..."}]}
        JsonObject jsonObject = Json.createObjectBuilder().add(
            "_links",
            Json.createArrayBuilder().add(
                Json.createObjectBuilder()
                    .add("_self", todoByIdPath.toString())
                    .add("_others", listPath.toString()).build()
            ).build()).build();
        return Response.ok(jsonObject.toString()).header("links", jsonObject.toString()).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@NotNull @PathParam("id") Long id) {
        return Response.ok(queryService.findTodoById(id)).build();
    }

    @GET
    @Path("list")
    public Response listTodo() {
        return Response.ok(queryService.findAllTodos()).build();
    }

    @GET
    @Path("find")
    public Response findTodoById(@NotNull @QueryParam("id") Long id) {
        return Response.ok(queryService.findAllTodos()).build();
    }

    @PUT
    @Path("mark")
    public Response markTodoAsCompleted(@NotNull @QueryParam("id") Long id) {
        queryService.markTodoAsCompleted(id);
        return Response.ok().build();
    }

    @GET
    @Path("completed")
    public Response getAllCompletedTodos() {
        return Response.ok(queryService.getAllCompletedTodos()).build();
    }

    @GET
    @Path("uncompleted")
    public Response getAllUncompletedTodos() {
        return Response.ok(queryService.getAllUncompletedTodos()).build();
    }

    @GET
    @Path("due-date")
    public Response getTodosByDueDate(@NotNull @QueryParam("date") @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$") String date) {
        LocalDate dueDate = LocalDate.parse(date);
        return Response.ok(queryService.getTodosByDueDate(dueDate)).build();
    }

    @PUT
    @Path("archive")
    public Response archiveTodo(@NotNull @QueryParam("id") Long id) {
        queryService.markTodoAsArchived(id);
        return Response.ok().build();
    }

}
