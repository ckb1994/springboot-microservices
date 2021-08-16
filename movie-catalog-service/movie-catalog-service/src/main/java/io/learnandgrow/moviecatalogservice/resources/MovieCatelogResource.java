package io.learnandgrow.moviecatalogservice.resources;

import io.learnandgrow.moviecatalogservice.models.CatalogItem;
import io.learnandgrow.moviecatalogservice.models.Movie;
import io.learnandgrow.moviecatalogservice.models.Rating;
import io.learnandgrow.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatelogResource {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        UserRating userRating = restTemplate.getForObject("http://rating-data-service/ratingdata/users/"+userId, UserRating.class);
        List<Rating> ratings = userRating.getUserRating();

        return  ratings.stream().map(rating -> {
            //Using RestTemplate way - may gonna depricated in future
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "Test", rating.getRating());
        }).collect(Collectors.toList());

    }
}
//Using Web Client
          /*  Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block(); */