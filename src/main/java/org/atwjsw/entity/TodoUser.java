package org.atwjsw.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "TodoUserTable")
// tU: alias. can be anything; :email is named parameter;
@NamedQuery(name = TodoUser.FIND_TODO_USER_BY_EMAIL, query = "select tU from TodoUser tU where tU.email = :email")
// = select * from TodoUserTable
@NamedQuery(name = TodoUser.FIND_ALL_TODO_USERS, query = "select todoUser from TodoUser todoUser order by todoUser.fullName")
@NamedQuery(name = TodoUser.FIND_TODO_USER_BY_ID, query = "select t from TodoUser t where t.id = :id and t.email = :email")
@NamedQuery(name = TodoUser.FIND_TODO_BY_NAME, query = "select t from TodoUser t where t.fullName like :name")
public class TodoUser extends AbstractEntity implements Serializable {

    public static final String FIND_TODO_USER_BY_EMAIL = "TodoUser.findByEmail";
    public static final String FIND_ALL_TODO_USERS = "TodoUser.findAll";
    public static final String FIND_TODO_USER_BY_ID = "TodoUser.findByIdAndEmail";
    public static final String FIND_TODO_BY_NAME = "TodoUser.findByName";

    @Column(length = 100)
    @NotEmpty(message = "An email must be set")
    @Email(message = "Email must be in the format user@domain.com")
    private String email; //varchar 100

    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, max = 500, message = "Password must be at minimum of 8 character, max 500 characters.")
//    @Pattern(regexp = "^[$%&#]*[A-Z]*[a-z]*[0-9]*", message = "Password must have at least one upper case, one lower case" +
//        ", a digit and must contain at least one of $%&#!")
    private String password; //varchar 255

    @NotEmpty(message = "Name must be set")
    @Size(min = 2, max = 100, message = "Name must be at minimum of 2 character, max 100 characters.")
    private String fullName;

    private String salt;

//    @OneToMany // in production, normally don't do the oneToMany side due to performance consideration
//    private final Collection<Todo> todos = new ArrayList<>();
}
