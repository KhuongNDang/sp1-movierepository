package app.daos;

import app.entities.Director;
import jakarta.persistence.EntityManager;

import java.util.List;

public class DirectorDAO {

    private final EntityManager em;

    public DirectorDAO(EntityManager em){
        this.em = em;
    }

    public void save(Director director){
        em.merge(director);
    }

    public Director find(int id){
        return em.find(Director.class, id);
    }

    public void delete(int id){
        Director director = find(id);
        if (director != null){
            em.remove(director);
        }
    }

    public List<Director> findAll(){
        return em.createQuery("SELECT d FROM Director d", Director.class).getResultList();
    }

}
