package com.ank.reactivews.other;

import com.ank.reactivews.model.Person;
import com.ank.reactivews.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Log4j2
@RequiredArgsConstructor
public class SampleDataInitializer {

    private final PersonRepository personRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void fill(){

      var just = Flux.just("Ankit", "Ayush", "Jack", "Kevin", "Josh")
              .map(name -> new Person(null,name))
              .flatMap(this.personRepository::save);

      this.personRepository.deleteAll()
              .thenMany(just)
              .thenMany(this.personRepository.findAll())
              .subscribe(log::info  );
    }

}
