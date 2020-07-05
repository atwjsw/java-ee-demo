package org.atwjsw.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NamedQuery(name = Todo.FIND_ALL_TODOS_BY_OWNER_EMAIL, query = "select todo from Todo todo where todo.todoOwner.email = :email")
public class Todo extends AbstractEntity{

    public static final String FIND_ALL_TODOS_BY_OWNER_EMAIL = "Todo.findAllByEmail";

    @NotEmpty(message = "A Todo task must be set")
    @Size(min = 3, max = 140, message = "The minimum character length should be 3 and max 140.")
    private String task;

    private LocalDate dateCreated;

    @NotNull(message = "Due date must be set")
    @FutureOrPresent(message = "Due date must be in the present or future.")
    @JsonbDateFormat(value = "yyyy-MM-dd")
    private LocalDate dueDate; //yyyy-mm-dd

    private boolean completed;
    private boolean archived;
    private boolean remind;

    @ManyToOne // only do the owning side
    @JoinColumn(name = "TodoUser_Id")
    private TodoUser todoOwner; // fk field default to todoOwner_id

    @PrePersist
    private void init() {
        setDateCreated(LocalDate.now());
    }
}
