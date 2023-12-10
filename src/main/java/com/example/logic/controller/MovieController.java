package com.example.logic.controller;

import com.example.logic.model.dto.MovieDto;
import com.example.logic.model.dto.RatedDto;
import com.example.logic.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {

    private MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping("/getMovie")
    public List<MovieDto> getMovie(@RequestParam final String movieName) {
        return service.getMovieDto(movieName);
    }

    @GetMapping("/topRated")
    public List<RatedDto> getTopRated(@RequestParam final String genre, @RequestParam int limit, @RequestParam int offset) {
        return service.getTopRatedMovie(genre, offset, limit);
    }

    @GetMapping("/degree-of-separation")
    public Object getDegreeOfSeparation(@RequestParam String name) {
        return service.findDegreeOfSeparation(name);
    }

}
