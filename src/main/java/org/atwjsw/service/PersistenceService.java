package org.atwjsw.service;

import org.atwjsw.entity.Todo;
import org.atwjsw.entity.TodoUser;

import javax.annotation.PostConstruct;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;

// stateless: state (member fields) not persisted across invocation. e.g. user initialize the EJB with certain
// values, and use the EJB later in some other places, it is not guaranteed the initialized values will be the same
// can be pooled, better performance
// stateful: keep state across invocation. need to be tracked each user associated with EJB
// singleton: only one instance across the application, from startup to shutdown
@DataSourceDefinition(
    name = "java:app/Todo/MyDS",
    className = "org.sqlite.SQLiteDataSource",
    url = "jdbc:sqlite:C:/Users/wenda/Documents/sql/todo.db"
)
@Stateless
public class PersistenceService {

//    @Inject
//    private MySession mySession;

    @Inject
    QueryService queryService;

    @Inject
    SecurityUtil securityUtil;

    @PersistenceContext(name = "pu")
    EntityManager em;

    @Context
    SecurityContext securityContext;

    public TodoUser saveTodoUser(TodoUser todoUser) {

        if (todoUser.getId() == null && queryService.countTodoUserByEmail(todoUser.getEmail()) == 0) {
            Map<String, String> credentialMap = securityUtil.hashPassword(todoUser.getPassword());
            todoUser.setPassword(credentialMap.get("hashedPassword"));
            todoUser.setSalt(credentialMap.get("salt"));
            em.persist(todoUser);
        }
        return todoUser;
    }

    public TodoUser updateTodoUser(TodoUser todoUser) {
        if (todoUser.getId() != null && queryService.countTodoUserByEmailAndId(todoUser.getEmail(), todoUser.getId()) == 1) {
            em.merge(todoUser);
        }
        return todoUser;
    }

    public TodoUser updateTodoUserEmail(Long id, String email) {

        if (queryService.countTodoUserByEmail(email) == 0) {
            TodoUser todoUser = queryService.findTodoUser(id);
            if (todoUser != null) {
                todoUser.setEmail(email);
                em.merge(todoUser);
                return todoUser;
            }
        }
        return null;
    }

    public Todo saveTodo(Todo todo) {

        TodoUser todoUserByEmail = queryService.findTodoUserByEmail(securityContext.getUserPrincipal().getName());

        if (todo.getId() == null && todoUserByEmail !=  null) {
            todo.setTodoOwner(todoUserByEmail);
            em.persist(todo);
        } else {
            em.merge(todo);
        }
        return todo;
    }

}
