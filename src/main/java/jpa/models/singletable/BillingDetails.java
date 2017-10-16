package jpa.models.singletable;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "StBillingDetails")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "BD_TYPE")
public abstract class BillingDetails {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull // Ignored by Hibernate for schema generation!
    @Column(nullable = false)
    protected String owner;

    // ...

    protected BillingDetails() {
    }

    protected BillingDetails(String owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
