package com.movieflix.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class MovieDto {
    private Integer movieId;

    @NotBlank(message = "Please Provide movie's title.")
    private String title;

    @NotBlank(message = "Please Provide movie's Director.")
    private String director;

    @NotBlank(message = "Please Provide movie's Studio.")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Please Provide movie's Poster.")
    private String poster;

    @NotBlank(message = "Please Provide Poster's URL.")
    private String posterUrl;

}
