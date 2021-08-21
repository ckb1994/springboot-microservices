package io.learnandgrow.moviecatalogservice.resources;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.learnandgrow.moviecatalogservice.models.CatalogItem;
import io.learnandgrow.moviecatalogservice.models.Movie;
import io.learnandgrow.moviecatalogservice.models.Rating;
import io.learnandgrow.moviecatalogservice.models.UserRating;
import io.learnandgrow.moviecatalogservice.services.MovieInfo;
import io.learnandgrow.moviecatalogservice.services.UserRatingInfo;
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
    WebClient.Builder webClientBuilder;

    @Autowired
    MovieInfo movieInfo;

    @Autowired
    UserRatingInfo userRatingInfo;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        //splitted this to another method
        UserRating userRating = userRatingInfo.getUserRating(userId);
        List<Rating> ratings = userRating.getUserRating();

        return  ratings.stream()
                .map(rating -> movieInfo.getCatalogItem(rating))
                .collect(Collectors.toList());

    }





}
//Using Web Client
          /*  Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block(); */