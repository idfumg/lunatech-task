package com.example.logic.model.repository;

import com.example.logic.model.entity.TitlePrincipal;
import com.example.logic.model.entity.TitlePrincipalId;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TitlePrincipalRepository extends JpaRepository<TitlePrincipal, TitlePrincipalId> {
    @Query("SELECT distinct t.tconst, t.nconst FROM TitlePrincipal t WHERE t.nconst IN (:principalsIds)")
    List<Tuple> getMoviesWhereIdsWereActors(final Collection<String> principalsIds);

    @Query("SELECT distinct tp.tconst, tp.nconst FROM TitlePrincipal tp LEFT JOIN TitleBasic tb ON tp.tconst = tb.tconst WHERE tp.nconst IN (:principalsIds) AND tb.startyear >= :sinceYear")
    List<Tuple> getMoviesWhereIdsWereActorsAndSinceYear(final Collection<String> principalsIds, final int sinceYear);

    @Query("SELECT distinct tp.nconst FROM TitlePrincipal tp LEFT JOIN NameBasic nm ON tp.nconst = nm.nconst WHERE tp.tconst IN (:movieIds) AND nm.deathyear >= :deadAfterYear")
    List<String> getActorsFromTheMoviesByIdsAndDeadAfterYear(final Collection<String> movieIds, final int deadAfterYear);

    @Query("SELECT distinct t.nconst FROM TitlePrincipal t WHERE t.tconst IN (:movieIds)")
    List<String> getActorsFromTheMoviesByIds(final Collection<String> movieIds);
}