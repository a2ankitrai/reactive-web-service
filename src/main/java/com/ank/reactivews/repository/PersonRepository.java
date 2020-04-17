package com.ank.reactivews.repository;

import com.ank.reactivews.model.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PersonRepository extends ReactiveCrudRepository<Person, String> {
}
