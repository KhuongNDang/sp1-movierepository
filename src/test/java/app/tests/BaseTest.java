package app.tests;

import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    protected EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeEach
    void setUp() {
        // Use HibernateConfig to get a test EntityManagerFactory
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();

    }
}
