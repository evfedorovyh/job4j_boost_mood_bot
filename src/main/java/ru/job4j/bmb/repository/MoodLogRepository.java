package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {

    List<MoodLog> findAll();
}
