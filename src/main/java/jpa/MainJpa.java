package jpa;

import jpa.models.*;
import org.hibernate.Session;

import javax.persistence.Embedded;
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

        embeddedType();

    }

    private static void embeddedType() {
        runJpaCode(em -> {

            Address address = new Address("Nahid sharghi", "12314","Tehran" );
            User user = new User("Dotin", address);

            em.persist(user);

            return null;
        });
    }

    private static void subselect() {
        runJpaCode(em ->{
           long item1Id = initItemBidSummaryDb(em);

//            ItemBidSummary itemBidSummary = em.find(ItemBidSummary.class, item1Id);

            Query query = em.createQuery(
                    "select ibs from ItemBidSummary ibs where ibs.itemId = :id"
            );
            ItemBidSummary itemBidSummary =
                    (ItemBidSummary)query.setParameter("id", item1Id).getSingleResult();

            System.out.println("number of bid = " +itemBidSummary.getNumberOfBids());

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
