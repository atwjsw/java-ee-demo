package org.atwjsw.rest;

import org.atwjsw.config.SecureAuth;
import org.atwjsw.entity.Todo;
import org.atwjsw.entity.TodoUser;
import org.atwjsw.service.PersistenceService;
import org.atwjsw.service.QueryService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @POST
    @Path("create")
    public Response createTodo(Todo todo) {
        persistenceService.saveTodo(todo);
        return Response.ok(todo).build();
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
