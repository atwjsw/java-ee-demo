package org.atwjsw.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.atwjsw.service.MySession;
import org.atwjsw.service.SecurityUtil;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

@Provider // register filter with JAX-RS runtime
@SecureAuth // Bind SecureAuth annotation with this filter. when @SecureAuth is used, securityFilter will be applied to that class
@Priority(Priorities.AUTHENTICATION) // higher priority. run before others
public class SecurityFilter implements ContainerRequestFilter {

    public static final String BEARER = "Bearer ";

    @Inject
    Logger logger;

    @Inject
    SecurityUtil securityUtil;

    @Inject
    MySession mySession;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authString = requestContext.getHeaderString(AUTHORIZATION);

        if (authString == null || authString.isEmpty() || !authString.startsWith(BEARER)) {
            logger.log(Level.WARNING, "No valid string token found");

            JsonObject jsonObject = Json.createObjectBuilder().add("error-message", "No valid string token found").build();

            throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).entity(jsonObject).build());
        }

        String token = authString.substring(BEARER.length());


        try {
            Key key = securityUtil.generateKey();
//            Jwts.parser().setSigningKey(key).parse(token);
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);

             // instantiate a new SecurityContext
            SecurityContext originalSecurityContext = requestContext.getSecurityContext();
            SecurityContext newSecurityContext = new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return claimsJws.getBody().getSubject();
                        }
                    };
                }

                @Override
                public boolean isUserInRole(String role) {
                    return originalSecurityContext.isUserInRole(role);
                }

                @Override
                public boolean isSecure() {
                    return originalSecurityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return originalSecurityContext.getAuthenticationScheme();
                }
            };

            requestContext.setSecurityContext(newSecurityContext);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Invalid jwt token", e);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }



    }
}
