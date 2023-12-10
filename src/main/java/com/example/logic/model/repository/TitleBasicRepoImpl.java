package com.example.logic.model.repository;

import com.example.logic.model.entity.TitleBasic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@ApplicationScope
@Service
public class TitleBasicRepoImpl {

    @PersistenceContext
    private EntityManager manager;

    public List<TitleBasic> getTopRated(String genre, int limit, int offset) {
        return manager
                .createQuery("SELECT t from TitleBasic t " +
                        "LEFT JOIN TitleRating r on t.tconst = r.tconst " +
                        "WHERE t.genres = ?1 and r.averagerating is not null " +
                        "ORDER BY r.averagerating desc", TitleBasic.class)
                .setParameter(1, genre)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
