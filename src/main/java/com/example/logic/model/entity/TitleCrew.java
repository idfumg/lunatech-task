package com.example.logic.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "title_crew")
public class TitleCrew {
    @Id
    @Column(name = "tconst", nullable = false, length = 10)
    private String tconst;

    @Column(name = "directors", length = 500)
    private String directors;

    @Column(name = "writers", length = 500)
    private String writers;
}