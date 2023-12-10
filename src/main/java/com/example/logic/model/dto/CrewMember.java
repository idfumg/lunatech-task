package com.example.logic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class CrewMember {
    private String name;
    private String role;
    private String charactersPlayed;
    private String id;
}