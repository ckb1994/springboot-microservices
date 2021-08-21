package io.learnandgrow.movieinfoservice.resources;

import io.learnandgrow.movieinfoservice.models.Movie;
import io.learnandgrow.movieinfoservice.models.MovieSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId){
        //Userd https://www.themoviedb.org/settings/api
        MovieSummary movieSummary = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"+ movieId+
                "?api_key="+apiKey, MovieSummary.class);
        System.out.println("Movie Summary "+ movieSummary);
        return new Movie(movieId,movieSummary.getTitle(),movieSummary.getOverview());
    }
}
