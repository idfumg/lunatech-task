package com.example.logic.model.repository;

import com.example.logic.model.entity.NameBasic;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Set;

public interface NameBasicRepository extends JpaRepository<NameBasic, String> {

    @Query("select nb.nconst, nb.primaryname from NameBasic nb where nb.nconst in :ids")
    List<Tuple> findPrimaryNamesByIds(Set<String> ids);

    @Query("SELECT n FROM NameBasic n WHERE n.primaryname = :primaryName")
    List<NameBasic> findByPrimaryname(final String primaryName);

    @Query("SELECT n FROM NameBasic n WHERE n.primaryname = :primaryName AND (n.deathyear >= :endYear OR n.deathyear IS null)")
    List<NameBasic> findByPrimarynameAndAliveSince(final String primaryName, final int endYear);

}