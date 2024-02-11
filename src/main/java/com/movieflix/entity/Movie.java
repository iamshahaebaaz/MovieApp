package com.movieflix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false)
    @NotBlank(message = "Please Provide movie's title.")
    private String title;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please Provide movie's Director.")
    private String director;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please Provide movie's Studio.")
    private String studio;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;

    @Column(nullable = false, length = 200)
    private Integer releaseYear;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please Provide movie's Poster.")
    private String poster;


}
