package com.movieflix.services;

import com.movieflix.dto.MovieDto;
import com.movieflix.entity.Movie;
import com.movieflix.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Service
public class MovieServiceImpl implements  MovieService{

    private final  FileService fileService;
    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }
    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
//        1. upload the file.
        String uploadedFile = fileService.uploadFile(path, file);


//        2. set the value of field 'poster' as file name.
        movieDto.setPoster(uploadedFile);

//        3. map dto to movie object
        Movie movie = new Movie(
                movieDto.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

//        4. save the movie object
        Movie savedMovie = movieRepository.save(movie);

//        5. generate the PosterUrl
        String posterUrl = baseUrl + "/file/" + uploadedFile;

//        6. map movie object to dto object and return it.
        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        return null;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return null;
    }
}
