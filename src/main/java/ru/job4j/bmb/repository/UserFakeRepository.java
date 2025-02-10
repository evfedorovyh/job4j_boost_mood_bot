package ru.job4j.bmb.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.User;

import java.util.ArrayList;
import java.util.List;

@Profile("test")
@Repository
public class UserFakeRepository extends CrudRepositoryFake<User, Long> implements UserRepository {

    @Override
    public List<User> findAll() {
        return new ArrayList<>(memory.values());
    }
}
