package jpa.models.batch;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity(name = "FUser")
@org.hibernate.annotations.BatchSize(size = 31)
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull
    protected String username;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    // ...
}
