package com.example.logic.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "title_principals")
public class TitlePrincipal {
    private static final long serialVersionUID = 5998649134999629146L;

    @Id
    @Column(name = "tconst", nullable = false, length = 10)
    private String tconst;

    @Column(name = "ordering", nullable = false)
    private Integer ordering;

    @Column(name = "nconst", nullable = false)
    private String nconst;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "job", length = 300)
    private String job;

    @Column(name = "characters")
    private String characters;

}