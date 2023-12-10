package com.example.logic.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "name_basics")
public class NameBasic {
    @Id
    @Column(name = "nconst", nullable = false, length = 10)
    private String nconst;

    @Column(name = "primaryname", length = 110)
    private String primaryname;

    @Column(name = "birthyear")
    private Integer birthyear;

    @Column(name = "deathyear")
    private Integer deathyear;

    @Column(name = "primaryprofession", length = 200)
    private String primaryprofession;

    @Column(name = "knownfortitles", length = 100)
    private String knownfortitles;

}