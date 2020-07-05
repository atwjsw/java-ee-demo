package org.atwjsw.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.atwjsw.config.SecureAuth;
import org.atwjsw.config.SecurityFilter;
import org.atwjsw.entity.TodoUser;
import org.atwjsw.service.MySession;
import org.atwjsw.service.PersistenceService;
import org.atwjsw.service.QueryService;
import org.atwjsw.service.SecurityUtil;

import javax.inject.Inject;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoUserRest {

    @Inject
    PersistenceService persistenceService;

    @Inject
    QueryService queryService;

    @Inject
    SecurityUtil securityUtil;

    @Context
    UriInfo uriInfo;

    @Inject
    Logger logger;

//    @Inject
//    MySession mySession;

    @Path("create")
    @POST
    public Response createTodoUser(@NotNull @Valid TodoUser todoUser) {
        persistenceService.saveTodoUser(todoUser);
        return Response.ok(todoUser).build();
    }

    @Path("update")
    @PUT
    @SecureAuth
    public Response updateTodoUser(@NotNull @Valid TodoUser todoUser) {
        persistenceService.updateTodoUser(todoUser);
        return Response.ok(todoUser).build();
    }

    @Path("find/{email}")
    @GET
    @SecureAuth
    public TodoUser findTodoUserByEmail(@NotNull @PathParam("email") String email) {
        return queryService.findTodoUserByEmail(email);
    }

    @Path("query")
    @GET
    @SecureAuth
    public TodoUser findTodoUserByEmailQueryParam(@NotNull @QueryParam("email") String email) {
        return queryService.findTodoUserByEmail(email);
    }

    @Path("search")
    @GET
    @SecureAuth
    public Response searchTodoUserByEmail(@NotNull @QueryParam("name") String name) {;
        return Response.ok(queryService.findTodoUsersByName(name)).build();
    }

    @Path("count")
    @GET
    public Response countTodoUserByEmail(@NotNull @QueryParam("email") String email) {
        return Response.ok(queryService.countTodoUserByEmail(email)).build();
    }

    @Path("list")
    @GET
    @SecureAuth
    public Response listAlTodoUsers() {
        return Response.ok(queryService.findAllTodoUsers()).build();
    }

    @Path("update-email")
    @PUT
    @SecureAuth
    public Response updateEmail(@NotNull @QueryParam("id") Long id, @NotNull @QueryParam("email") String email) {
        return Response.ok(persistenceService.updateTodoUserEmail(id, email)).build();
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@NotEmpty(message = "Email field must be set") @FormParam("email") String email,
                          @NotEmpty(message = "Password field must be set") @FormParam("password") String password) {
        //Authenticate user
        //generate token
        //return token in response header to client
        if (!securityUtil.authenticateUser(email, password)) {
            throw new SecurityException("Email or password is invalid");
        }
        String token = getToken(email);
//        mySession.setEmail(email);

        return Response.ok().header(AUTHORIZATION, SecurityFilter.BEARER + " " + token).build();
    }

    private String getToken(String email) {
        Key key = securityUtil.generateKey();

        String token = Jwts.builder()
            .setSubject(email)
            .setIssuer(uriInfo.getAbsolutePath().toString())
            .setIssuedAt(new Date())
            .setExpiration(securityUtil.toDate(LocalDateTime.now().plusMinutes(15)))
            .signWith(SignatureAlgorithm.HS512, key)
            .setAudience(uriInfo.getBaseUri().toString()).compact();

        logger.log(Level.INFO,"Generated token is {0}", token);

        return token;
    }

}
