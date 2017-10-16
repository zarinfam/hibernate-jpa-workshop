package jpa;

import jpa.models.*;
import jpa.models.mappedsuperclass.BankAccount;
import jpa.models.mappedsuperclass.BillingDetails;
import jpa.models.mappedsuperclass.CreditCard;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static jpa.util.EMF.runJpaCode;

/**
 * @author Saeed Zarinfam
 */
public class MainJpa {

    public static void main(String[] args) {
        insertSampleBillingDetailsSingleTable();

        loadSingleTable().forEach(billingDetails -> System.out.println(billingDetails.getOwner()));
    }

    private static List<jpa.models.singletable.BillingDetails> loadSingleTable() {
        return runJpaCode(em -> em.createQuery("from StBillingDetails" ).getResultList());
    }

    private static List<BillingDetails> fetchBillingDetailsMappedSuperClass() {
        return runJpaCode(em -> em.createQuery("from " + BillingDetails.class.getName()).getResultList());
    }

    private static void insertSampleBillingDetails() {
        runJpaCode(em -> {

            BillingDetails billingDetails = new BankAccount("Ali", "123456", "Pasargad", "4124aas");
            em.persist(billingDetails);

            billingDetails = new CreditCard("Ali", "1231599123", "12", "1400");
            em.persist(billingDetails);

            return null;
        });
    }

    private static void insertSampleBillingDetailsSingleTable() {
        runJpaCode(em -> {

            jpa.models.singletable.BillingDetails billingDetails =
                    new jpa.models.singletable.BankAccount("Ali", "123456", "Pasargad", "4124aas");
            em.persist(billingDetails);

            billingDetails = new jpa.models.singletable.CreditCard("Ali", "1231599123", "12", "1400");
            em.persist(billingDetails);

            return null;
        });
    }

    private static void embeddedType() {
        runJpaCode(em -> {

            Address address = new Address("Nahid sharghi", "12314", "Tehran");
            Address billingAddress = new Address("Nahid gharbi", "32141", "Shiraz");
            User user = new User("Dotin", address);
            user.setBillingAddress(billingAddress);

            em.persist(user);

            return null;
        });
    }

    private static void subselect() {
        runJpaCode(em -> {
            long item1Id = initItemBidSummaryDb(em);

//            ItemBidSummary itemBidSummary = em.find(ItemBidSummary.class, item1Id);

            Query query = em.createQuery(
                    "select ibs from ItemBidSummary ibs where ibs.itemId = :id"
            );
            ItemBidSummary itemBidSummary =
                    (ItemBidSummary) query.setParameter("id", item1Id).getSingleResult();

            System.out.println("number of bid = " + itemBidSummary.getNumberOfBids());

            return null;
        });
    }

    private static long initItemBidSummaryDb(EntityManager em) {
        Item item1 = new Item();
        item1.setDescription("desc item1");
        item1.setName("item1");
        item1.setVerified(true);
        em.persist(item1);

        IntStream.range(1, 5).forEach(i -> em.persist(createBid(100 + i, item1)));

        return item1.getId();
    }

    private static Bid createBid(int ammout, Item item1) {
        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(ammout));
        bid.setItem(item1);
        return bid;
    }

    private static void validation() {
        runJpaCode(em -> {
            em.persist(new User("test-user"));
            return null;
        });
    }

    private static void hello() {
        Message m = runJpaCode(em -> {
            Message message = new Message();
            message.setText("Hello World!");
            em.persist(message);
            return message;
        });

        System.out.println(m.getId());

        List<Message> l = runJpaCode(em -> (List<Message>) em.unwrap(Session.class).createCriteria(Message.class).list());

        l.stream().forEach(message -> System.out.println(message.getId()));
    }

}
