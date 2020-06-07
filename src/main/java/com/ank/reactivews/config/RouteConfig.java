package com.ank.reactivews.config;

import com.ank.reactivews.model.HelloRequest;
import com.ank.reactivews.model.HelloResponse;
import com.ank.reactivews.model.Person;
import com.ank.reactivews.repository.PersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class RouteConfig {
    @Bean
    RouterFunction<ServerResponse> routes(PersonRepository personRepository, HelloProducer helloProducer){
        return route()
                .GET("/persons", serverRequest -> ok().body(personRepository.findAll(), Person.class))

                .GET("/greeting/{name}", r -> ok()
                        .body(helloProducer
                                .helloResponseMono(new HelloRequest(r.pathVariable("name"))), HelloResponse.class))

                .GET("/greetings/{name}", r -> ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(helloProducer
                                .helloResponseFlux(new HelloRequest(r.pathVariable("name"))), HelloResponse.class))
                .build();
    }
}
