package jpa;

import jpa.models.*;
import jpa.models.mappedsuperclass.BankAccount;
import jpa.models.mappedsuperclass.BillingDetails;
import jpa.models.mappedsuperclass.CreditCard;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static jpa.util.EMF.runJpaCode;

/**
 * @author Saeed Zarinfam
 */
public class MainJpa {

    public static void main(String[] args) {

        initialDbForFetch();

        Item item = new Item();
        item.setDescription("test");
        long id =insertItem(item).getId();

        runJpaCode(em -> {


//            em.persist(item);
//
            Session session = em.unwrap(Session.class);
//            session.setFlushMode(FlushMode.AUTO);

            Item item2 = new Item();
            item2.setName("test insert");

//            em.persist(item2);
            session.save(item2);

//            Item item1 = em.find(Item.class, id);
            Item item1 = session.get(Item.class, id);
            item1.setName("test load");


//            session.saveOrUpdate(item1);
//
//            Query query = em.createNativeQuery(
//                    "SELECT * from ITEM", Item.class
//            );

            SQLQuery query = session.createSQLQuery(
                    "SELECT * from FBID b")
                    .addEntity(jpa.models.batch.Bid.class)
                    ;
//            org.hibernate.Query query = session.createQuery(
//                    "from Item b");
//                    ;
//            Query query = em.createQuery(
//                    "from Item i")
//                    ;

//            Query query = em.createNativeQuery("SELECT * from FBID b", jpa.models.batch.Bid.class);

//            query.getResultList();
            query.list();

//            bids.forEach(bid -> System.out.println(bid.getId()));





            List<Object[]> tuples = session.createSQLQuery(
                    "SELECT * " +
                            "FROM FBID ph " +
                            "JOIN FITEM pr ON ph.ITEM_ID = pr.ID" )
                    .addEntity("bid", jpa.models.batch.Bid.class )
                    .addJoin( "pr", "bid.item")
//                    .setResultTransformer( Criteria.ROOT_ENTITY )
                    .list();

            for(Object[] tuple : tuples) {
            }



            return null;
        });
    }

    private static void initialDbForFetch() {
        runJpaCode(em -> {

            IntStream.rangeClosed(1, 100).forEach(i -> {
                jpa.models.batch.User seller
                        = new jpa.models.batch.User("seller-" + i);

                jpa.models.batch.Item item
                        = new jpa.models.batch.Item("item-" + i, seller);

                IntStream.rangeClosed(1, 10).forEach(j -> {
                    jpa.models.batch.User bidder
                            = new jpa.models.batch.User("bidder-" + j);

                    item.getBids().add(new jpa.models.batch.Bid(item, bidder, new BigDecimal(1000 + j)));
                });


                em.persist(item);
            });

            return null;
        });
    }

    private static void queryLockMode() {
        Item persistenceItem = insertItem(new Item());
        insertItem(new Item());
        insertItem(new Item());

        doAsync(() -> runJpaCode(em -> {
            sleep(1000);
            long totalPrice = 0;

            List<Item> items =
                    em.createQuery("from Item")
                            .setLockMode(LockModeType.PESSIMISTIC_READ)
                            .setHint("javax.persistence.lock.timeout", 5000)
                            .getResultList();

            for (Item item : items) {
                totalPrice += item.getId();
                sleep(1000);
            }


            return null;
        }));

        doAsync(() -> runJpaCode(em -> {
            Item item = em.find(Item.class, persistenceItem.getId(), LockModeType.PESSIMISTIC_WRITE);
            item.setName("New Name");
            sleep(2000);
            return null;
        }));
    }

    private static void doAsync(Supplier codeBlock) {
        CompletableFuture.supplyAsync(codeBlock);
    }


    private static void updateItem(Long id, Long waitTime) {
        CompletableFuture.supplyAsync(() -> runJpaCode(em -> {
            System.out.println("------start " + waitTime);
            Item item = em.find(Item.class, id);
            item.setName("New Name");

            sleep(waitTime);

            System.out.println("------end " + waitTime);

            return null;
        }));
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Item insertItem(Item item) {
        return runJpaCode(em -> {

            item.setName("Some Item");
            em.persist(item);
            Long ITEM_ID = item.getId();

            return item;
        });
    }


    private static void manyToMany() {
        runJpaCode(em -> {
            Category someCategory = new Category("Some Category");
            Category otherCategory = new Category("Other Category");

            Item someItem = new Item();
            someItem.setDescription("Yadegari madar bozorg");
            someItem.setName("Ghandon");

            Item otherItem = new Item();
            otherItem.setDescription("Yadegari pedar bozorg");
            otherItem.setName("Another Ghandon");

//            someCategory.getItems().add(someItem);
            someItem.getCategories().add(someCategory);
//            someCategory.getItems().add(otherItem);
            otherItem.getCategories().add(someCategory);
//            otherCategory.getItems().add(someItem);
            someItem.getCategories().add(otherCategory);

            em.persist(someCategory);
            em.persist(otherCategory);

            em.persist(someItem);
            em.persist(otherItem);

            return null;
        });
    }

    private static void bidItemOneToOne() {
        runJpaCode(em -> {
            Item item = new Item();
            item.setDescription("Yadegari madar bozorg");
            item.setName("Ghandon");

            Bid bid = new Bid();
            bid.setAmount(BigDecimal.valueOf(100));

            Bid bid2 = new Bid();
            bid2.setAmount(BigDecimal.valueOf(200));

            item.addBid(bid);
            item.addBid(bid2);

            em.persist(item);

            em.remove(item);

            return null;
        });
    }

    private static List<jpa.models.singletable.BillingDetails> loadSingleTable() {
        return runJpaCode(em -> em.createQuery("from StBillingDetails").getResultList());
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
