package com.example.logic.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class TitlePrincipalId implements Serializable {
    private static final long serialVersionUID = 5998649134999629146L;
    @Column(name = "tconst", nullable = false, length = 10)
    private String tconst;

    @Column(name = "ordering", nullable = false)
    private Integer ordering;

    @Column(name = "nconst", nullable = false, length = 10)
    private String nconst;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TitlePrincipalId entity = (TitlePrincipalId) o;
        return Objects.equals(this.tconst, entity.tconst) &&
                Objects.equals(this.ordering, entity.ordering) &&
                Objects.equals(this.nconst, entity.nconst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tconst, ordering, nconst);
    }

}