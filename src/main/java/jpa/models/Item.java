package jpa.models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.*;

@Entity
public class Item {

    /* 
       The <code>Item</code> entity defaults to field access, the <code>@Id</code> is on a field. (We
       have also moved the brittle <code>ID_GENERATOR</code> string into a constant.)
     */
    @Id
    @GeneratedValue
    protected Long id;

    @ManyToMany(mappedBy = "items")
    protected Set<Category> categories = new HashSet<Category>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST)
    @org.hibernate.annotations.OnDelete(
            action = org.hibernate.annotations.OnDeleteAction.CASCADE
    )
    private Set<Bid> bids = new HashSet<>();

    @org.hibernate.annotations.Type(type = "yes_no")
    protected boolean verified = false;

    // JPA says @Temporal is required but Hibernate will default to TIMESTAMP without it
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    protected Date createdOn;

    // Java 8 API
    // protected Instant reviewedOn;

    @Basic(fetch = FetchType.LAZY) // Defaults to EAGER
    protected String description;

    @Basic(fetch = FetchType.LAZY)
    @Column(length = 131072) // 128 kilobyte maximum for the picture
    protected byte[] image; // Maps to SQL VARBINARY type

    @Lob
    protected Blob imageBlob;

    @org.hibernate.annotations.Formula(
        "substr(DESCRIPTION, 1, 12) || '...'"
    )
    protected String shortDescription;

    @org.hibernate.annotations.Formula(
        "(select avg(b.AMOUNT) from BID b where b.ITEM_ID = ID)"
    )
    protected BigDecimal averageBidAmount;

    @Column(name = "IMPERIALWEIGHT")
    @org.hibernate.annotations.ColumnTransformer(
        read = "IMPERIALWEIGHT / 2.20462",
        write = "? * 2.20462"
    )
    protected double metricWeight;

//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(insertable = false, updatable = false)
//    @org.hibernate.annotations.Generated(
//        org.hibernate.annotations.GenerationTime.ALWAYS
//    )
//    protected Date lastModified;
//
//    @Column(insertable = false)
//    @org.hibernate.annotations.ColumnDefault("1.00")
//    @org.hibernate.annotations.Generated(
//        org.hibernate.annotations.GenerationTime.INSERT
//    )
//    protected BigDecimal initialPrice;

    /* 
        The <code>@Access(AccessType.PROPERTY)</code> setting on the <code>name</code> field switches this
        particular property to runtime access through getter/setter methods by the JPA provider.
     */
    @Access(AccessType.PROPERTY)
    @Column(name = "ITEM_NAME") // Mappings are still expected here!
    protected String name;

    /* 
        Hibernate will call <code>getName()</code> and <code>setName()</code> when loading and storing items.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name =
            !name.startsWith("AUCTION: ") ? "AUCTION: " + name : name;
    }

    public Long getId() { // Optional but useful
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public BigDecimal getAverageBidAmount() {
        return averageBidAmount;
    }

    public double getMetricWeight() {
        return metricWeight;
    }

    public void setMetricWeight(double metricWeight) {
        this.metricWeight = metricWeight;
    }

//    public Date getLastModified() {
//        return lastModified;
//    }
//
//    public BigDecimal getInitialPrice() {
//        return initialPrice;
//    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Blob getImageBlob() {
        return imageBlob;
    }

    public void setImageBlob(Blob imageBlob) {
        this.imageBlob = imageBlob;
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public void setBids(Set<Bid> bids) {
        this.bids = bids;
    }

    public void addBid(Bid bid) {
        this.bids.add(bid);
        bid.setItem(this);
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

}
