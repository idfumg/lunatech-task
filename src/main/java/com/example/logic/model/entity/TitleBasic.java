package com.example.logic.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "title_basics")
public class TitleBasic {
    @Id
    @Column(name = "tconst", nullable = false, length = 10)
    private String tconst;

    @Column(name = "titletype", length = 20)
    private String titletype;

    @Column(name = "primarytitle", length = 500)
    private String primarytitle;

    @Column(name = "originaltitle", length = 500)
    private String originaltitle;

    @Column(name = "isadult")
    private Boolean isadult;

    @Column(name = "startyear")
    private Integer startyear;

    @Column(name = "endyear")
    private Integer endyear;

    @Column(name = "runtimeminutes")
    private Integer runtimeminutes;

    @Column(name = "genres", length = 200)
    private String genres;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tconst")
    private TitleCrew crew;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "tconst")
    private Set<TitlePrincipal> principals;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tconst")
    private TitleRating rating;
}