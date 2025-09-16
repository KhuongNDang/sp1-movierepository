package app;

import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("JPA Beer demo");

        Beer b1 = Beer.builder()
                .name("Svanneke Classic")
                .brewery("Svanneke Bryghus")
                .alc(4.8)
                .type("Pilsner")
                .build();

        Beer b2 = Beer.builder()
                .name("Royal Export")
                .brewery("Royal Breweries")
                .alc(5.6)
                .type("Pilsner")
                .build();

        // Svarer til en fabrik, der kan lave en ConnectionPool
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Dette er vores connection (forbindelse)
        EntityManager em = emf.createEntityManager();

        System.out.println("Før: " + b1);

        em.getTransaction().begin();
             em.persist(b1);
             em.persist(b2);
        em.getTransaction().commit();

        em.getTransaction().begin();
            b1.setAlc(4.6);
            em.merge(b1);

            // Forespørgel til DB om der findes en øl med navn "Royal Export":
        TypedQuery query = em.createQuery("select b from Beer b where b.name = :name", Beer.class).setParameter("name", "Royal Export");
        List<Beer> beers = query.getResultList();

        if (beers.size() > 0) {
            System.out.println("Hov, den findes i forvejen");
            System.out.println("Øl: " + beers.get(0).getBrewery());
        } else {
            // indsæt
        }


        em.getTransaction().commit();

        System.out.println("Efter: " + b1);

        em.close();
        emf.close();



    }
}