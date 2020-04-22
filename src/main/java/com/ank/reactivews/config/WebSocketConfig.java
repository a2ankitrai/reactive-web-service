package com.ank.reactivews.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class WebSocketConfig {

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler webSocketHandler){
        return new SimpleUrlHandlerMapping(Map.of("/ws/socketHello", webSocketHandler),10);
//        return new SimpleUrlHandlerMapping(){
//            @Override
//            public void setUrlMap(Map<String, ?> urlMap) {
//                super.setUrlMap(Map.of("/ws/socketHello", webSocketHandler));
//            }
//            @Override
//            public void setOrder(int order) {
//                super.setOrder(10);
//            }
//        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter(){
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(HelloProducer producer){
        return webSocketSession -> {
           Flux<WebSocketMessage> messageFlux =  webSocketSession.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .map(HelloRequest::new)
                    .flatMap(producer::hello)
                    .map(HelloResponse::getMessage)
                    .map(webSocketSession::textMessage)
                   .doOnEach(webSocketMessageSignal -> {
                       // this will log message on each processing of the websocket on client side
                           log.info(String.valueOf(webSocketMessageSignal.getType())) ;
                   })
                   // this will log message when client terminates the ws connection
                   .doFinally(signalType -> log.info("Finally: "+signalType));

           return webSocketSession.send(messageFlux);
        };
    }


}

@Data
@AllArgsConstructor
@NoArgsConstructor
class HelloResponse{
    private String message;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class HelloRequest{
    private String name;
}

@Component
class HelloProducer{
    Flux<HelloResponse> hello(HelloRequest request){
        return Flux.fromStream(
                Stream.generate(
                        () -> new HelloResponse("Namaskaram " + request.getName() + " @ " + Instant.now())
                ))
                .delayElements(Duration.ofSeconds(1));
                // .take(5); // if we want to limit the number of elements
    }
}
