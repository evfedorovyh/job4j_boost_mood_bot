package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.UserAdvice;

import java.util.List;

@Repository
public interface UserAdviceRepository extends CrudRepository<UserAdvice, Long> {

    @Override
    List<UserAdvice> findAll();
}
