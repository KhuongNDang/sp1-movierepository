package app.daos;

import app.entities.Actor;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ActorDAO {

    private final EntityManager em;

    public ActorDAO(EntityManager em){
        this.em = em;
    }

    public void save(Actor actor){
        em.merge(actor);
    }


    public Actor find(int id){
        return em.find(Actor.class, id);
    }

    public void delete(int id){
        Actor actor = find(id);
        if (actor != null){
            em.remove(actor);
        }
    }

    public List<Actor> finAll(){
        return em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();
    }

}
