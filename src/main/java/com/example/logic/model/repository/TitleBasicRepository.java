package com.example.logic.model.repository;

import com.example.logic.model.entity.TitleBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TitleBasicRepository extends JpaRepository<TitleBasic, String> {

    @Query("""
            SELECT distinct t FROM TitleBasic t
            left join TitleCrew c on t.tconst = c.tconst
            left join TitlePrincipal tp on t.tconst = tp.tconst
            WHERE t.originaltitle = :searchValue OR t.primarytitle = :searchValue
            GROUP BY t.tconst
            """)
    List<TitleBasic> findByNameWithCrewAndPrincipal(String searchValue);
}