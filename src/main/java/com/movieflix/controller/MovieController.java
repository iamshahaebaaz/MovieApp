package com.movieflix.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.dto.MovieDto;
import com.movieflix.exception.EmptyFileException;
import com.movieflix.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/v1/movie")
public class MovieController {


    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDtoObj) throws IOException {
        MovieDto movieDto = convertMovieDto(movieDtoObj);
        if(file.isEmpty()){
            throw new EmptyFileException("File is Empty. Please add file...");
        }
        return new ResponseEntity<>(movieService.addMovie(movieDto,file), HttpStatus.CREATED);
    }


    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){
    return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public  ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart String movieDtoObj,
                                                       @RequestPart MultipartFile file) throws IOException {
        if(file.isEmpty()) file =null;

        MovieDto movieDto = convertMovieDto(movieDtoObj);
        return ResponseEntity.ok( movieService.updateMovie(movieId, movieDto, file));

    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId)  {
        try {
            return ResponseEntity.ok( movieService.deleteMovie(movieId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private MovieDto convertMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.readValue(movieDtoObj, MovieDto.class);
    }

}
