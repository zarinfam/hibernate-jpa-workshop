package jpa.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Saeed Zarinfam
 */

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
