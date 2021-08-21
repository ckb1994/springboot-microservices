package io.learnandgrow.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.learnandgrow.moviecatalogservice.models.CatalogItem;
import io.learnandgrow.moviecatalogservice.models.Movie;
import io.learnandgrow.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfo {

    @Autowired
    RestTemplate restTemplate;


//
//    @HystrixCommand(fallbackMethod = "getFallBackCatelogItem",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
//                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
//                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
//                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
//            }
//    )
    @HystrixCommand(fallbackMethod = "getFallBackCatelogItem",
    commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
    }
)
    public CatalogItem getCatalogItem(Rating rating) {

        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+ rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), "Test", rating.getRating());
    }
    private CatalogItem getFallBackCatelogItem(Rating rating) {
        return  new CatalogItem("Movies name not found","",rating.getRating());
    }
}
