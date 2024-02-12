package com.movieflix.services;

import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.entity.Movie;
import com.movieflix.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        if(Files.exists(Paths.get(path + File.separator+ file.getOriginalFilename())) ){
            throw
                     new RuntimeException("File already exists! please enter another file name.!");
        }
        String uploadedFile = fileService.uploadFile(path, file);

//        2. set the value of field 'poster' as file name.
        movieDto.setPoster(uploadedFile);

//        3. map dto to movie object
        Movie movie = new Movie(
                null,
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

//        1. check the data in the db if exists fetch the data of given id
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie Not found with given Id"));
//        2. generate poster url
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

//        3. map to MovieDto object and return

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getAllMovies() {
//        1. fetch all data from db
        List<Movie> movies = movieRepository.findAll();

//        2. iterate through the list, Generate poster url for each movieobj.
//        and map to MovieDto obj
        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
//        1. Check if movie obejct exists with given movieId.
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie Not found with given Id"));
//        2. if file is null, do nothing.
//        if file is not null delete the existing file associated to the record.
//        and upload new file
        String fileName = mv.getPoster();
        if(file!=null){
            Files.deleteIfExists(Paths.get(path+ File.separator+ fileName));
            fileName = fileService.uploadFile(path, file);
        }

//        3. set movieDTo poster value according to step 2.
        movieDto.setPoster(fileName);

//        4. map this to movie object.
        Movie movie = new Movie(
                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
//        5. save movie object  -> return saved movie object
        Movie updatedMovie = movieRepository.save(movie);
//        6. generate poster Url.
        String posterUrl = baseUrl + "/file/"+ fileName;

//        7. map to movieDto and return it.
        return new MovieDto(
        movie.getMovieId(),
        movie.getTitle(),
        movie.getDirector(),
        movie.getStudio(),
        movie.getMovieCast(),
        movie.getReleaseYear(),
        movie.getPoster(),
        posterUrl
);
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
//        1. check the movie object is exist in db
        Movie mv = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie Not found with given Id"));
        Integer id = mv.getMovieId();
//        2. delete the file associated with this object.
            Files.deleteIfExists(Paths.get(path+File.separator+mv.getPoster()));

//        3. delete the movie object.
        movieRepository.delete(mv);
        return "Movie with ID: "+id+ " got deleted successfully...";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        // getting object of Pageable by passing pageNumber and pageSize.
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        // by calling findAll pageable we are getting the pages of Movie
        // which have information like total number of elements totalpages.

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        // retriving the content present in moviePages object.
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new  MoviePageResponse(movieDtos, pageNumber, pageSize,
                                moviePages.getTotalElements(),
                                moviePages.getTotalPages(),
                                moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = Sort.by(dir.equalsIgnoreCase("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        // by calling findAll pageable we are getting the pages of Movie
        // which have information like total number of elements totalpages.

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        // retriving the content present in moviePages object.
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);

        }

        return new  MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}
