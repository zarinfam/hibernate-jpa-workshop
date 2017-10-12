package jpa.models.tableperclass;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "TpcBillingDetails")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BillingDetails {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull
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
