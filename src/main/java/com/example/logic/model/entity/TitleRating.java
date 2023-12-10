package com.example.logic.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "title_ratings")
public class TitleRating {
    @Id
    @Column(name = "tconst", nullable = false, length = 10)
    private String tconst;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tconst", nullable = false)
    private TitleBasic titleBasics;

    @Column(name = "averagerating")
    private Double averagerating;

    @Column(name = "numvotes")
    private Integer numvotes;

}