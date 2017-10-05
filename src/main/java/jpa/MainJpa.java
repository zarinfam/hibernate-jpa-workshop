package jpa;

import jpa.models.Message;
import jpa.models.User;
import org.hibernate.Session;

import java.util.List;

import static jpa.util.EMF.runJpaCode;

/**
 * @author Saeed Zarinfam
 */
public class MainJpa {

    public static void main(String[] args) {

        validation();

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
