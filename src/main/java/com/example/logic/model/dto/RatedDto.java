package com.example.logic.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RatedDto {

    private String primaryTitle;
    private String originalTitle;
    private Double averagerating;
    private String titletype;
    private String genres;
}
