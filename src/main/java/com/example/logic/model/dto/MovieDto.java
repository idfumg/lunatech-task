package com.example.logic.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class MovieDto {

    private String primaryTitle;
    private String originalTitle;
    private Integer startYear;
    private Integer endYear;
    private Integer minutes;
    private List<String> genres;
    private List<CrewMember> crew;

}
