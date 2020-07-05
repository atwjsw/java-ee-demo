package org.atwjsw.service;

import lombok.extern.slf4j.Slf4j;
import org.atwjsw.entity.Todo;
import org.atwjsw.entity.TodoUser;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class) // test with Arqullian framework
public class PersistenceServiceTest {

    @Inject
    QueryService queryService;

    @Inject
    PersistenceService persistenceService;

    @Inject
    MySession mySession;

    @Inject
    Todo todo;

    @Inject
    TodoUser todoUser;

    // create an archive of the app and deploy to managed container i.e. payara server
    // start the container and run the test
    // un-deploy and shutdown the container
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addPackage(PersistenceService.class.getPackage())
            .addPackage(Todo.class.getPackage())
            .addAsResource("META-INF/persistence.xml","META-INF/persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() throws Exception {
        mySession.setEmail("bla@bla.com");

        todoUser.setEmail("bl@bla.com");
        todoUser.setPassword(UUID.randomUUID().toString());
        todoUser.setFullName("Donald Trump");

        todo.setDueDate(LocalDate.of(2020, 12, 31));
        todo.setTask("Master Jarkarta EE Development");
//        todo.setTodoOwner(todoUser);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void saveTodoUser() {
        persistenceService.saveTodoUser(todoUser);
//        persistenceService.saveTodo(todo);

        assertNotNull(todoUser.getId());
//        assertNotNull(todo.getTodoOwner().getId());
//        assertNotNull(todo.getDateCreated());

//        System.out.println(todo.getDateCreated());
//        System.out.println(todoUser.getId());
//        System.out.println(todo.getTodoOwner().getId());
    }

    @Test
    public void saveTodo() {



    }
}
