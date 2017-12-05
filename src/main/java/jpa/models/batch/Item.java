package jpa.models.batch;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "FItem")
public class Item {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull
    protected String name;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @org.hibernate.annotations.Fetch(
            org.hibernate.annotations.FetchMode.SELECT
    )
    protected User seller;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item", cascade = CascadeType.PERSIST)
    @org.hibernate.annotations.Fetch(
            org.hibernate.annotations.FetchMode.SELECT
    )
//    @org.hibernate.annotations.BatchSize(size = 5)
    protected Set<Bid> bids = new HashSet<>();

    public Item() {
    }

    public Item(String name, User seller) {
        this.name = name;
        this.seller = seller;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public void setBids(Set<Bid> bids) {
        this.bids = bids;
    }

    // ...
}
