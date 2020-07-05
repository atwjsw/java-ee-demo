package org.atwjsw.service;

import org.atwjsw.entity.Todo;
import org.atwjsw.entity.TodoUser;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Stateless
public class QueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    MySession mySession;

    @Inject
    SecurityUtil securityUtil;

    public TodoUser findTodoUserByEmail(String email) {

        try {
            return em.createNamedQuery(TodoUser.FIND_TODO_USER_BY_EMAIL, TodoUser.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NonUniqueResultException | NoResultException e) {
            return null;
        }
    }

    public List<TodoUser> findAllTodoUsers() {
        return em.createNamedQuery(TodoUser.FIND_ALL_TODO_USERS, TodoUser.class).getResultList();
    }

    public TodoUser findTodoUserById(Long id) {
        return em.createNamedQuery(TodoUser.FIND_TODO_USER_BY_ID, TodoUser.class)
            .setParameter("id", id)
            .setParameter("email", mySession.getEmail())
            .getSingleResult();
    }

    public TodoUser findTodoUser(Long id) {
        return em.find(TodoUser.class, id);
    }

    public Collection<TodoUser> findTodoUsersByName(String name) {
        return em.createNamedQuery(TodoUser.FIND_TODO_BY_NAME, TodoUser.class)
            .setParameter("name", "%" + name + "%").getResultList();
    }

    public Collection<Todo> findAllTodos(String email) {
        return em.createNamedQuery(Todo.FIND_ALL_TODOS_BY_OWNER_EMAIL, Todo.class)
            .setParameter("email", email).getResultList();
    }

    public Collection<Todo> findAllTodos() {
        return em.createNamedQuery(Todo.FIND_ALL_TODOS_BY_OWNER_EMAIL, Todo.class)
            .setParameter("email", mySession.getEmail()).getResultList();
    }

    public int countTodoUserByEmail(String email) {
        return (int)em.createNativeQuery("select count(id) from TodoUserTable where email = ?")
            .setParameter(1, email)
            .getSingleResult();
    }

    public int countTodoUserByEmailAndId(String email, Long id) {
        return (int)em.createNativeQuery("select count(id) from TodoUserTable where email = ? and id = ?")
            .setParameter(1, email)
            .setParameter(2, id)
            .getSingleResult();
    }

    public Todo findTodoById(Long id) {
        List<Todo> resultList = em.createQuery("select t from Todo t where t.id =:id and t.todoOwner.email = :email ", Todo.class)
            .setParameter("id", id)
            .setParameter("email", mySession.getEmail())
            .getResultList();

        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }



    public void markTodoAsCompleted(Long id) {
        Todo todo = findTodoById(id);
        if (todo != null) {
            todo.setCompleted(true);
            em.merge(todo);
        }
    }

    public List<Todo> getAllCompletedTodos() {
        return getTodosByState(true);
    }

    public List<Todo> getAllUncompletedTodos() {
        return getTodosByState(false);
    }

    private List<Todo> getTodosByState(boolean state) {
        return em.createQuery("select t from Todo t where t.todoOwner.email = :email and t.completed = :state", Todo.class)
            .setParameter("email", mySession.getEmail())
            .setParameter("state", state)
            .getResultList();
    }

    public List<Todo> getTodosByDueDate(LocalDate dueDate) {
        return em.createQuery("select t from Todo t where t.todoOwner.email = :email and t.dueDate <= :dueDate", Todo.class)
            .setParameter("email", mySession.getEmail())
            .setParameter("state", dueDate)
            .getResultList();
    }

    public void markTodoAsArchived(Long id) {
        Todo todo = findTodoById(id);
        if (todo != null) {
            todo.setArchived(true);
            em.merge(todo);
        }
    }

    public boolean authenticateUser(String email, String plainTextPassword) {

        TodoUser todoUser = findTodoUserByEmail(email);

        if (todoUser != null) {
            return securityUtil.passwordMatch(todoUser.getPassword(), todoUser.getSalt(), plainTextPassword);
        }
        return false;
    }
}
