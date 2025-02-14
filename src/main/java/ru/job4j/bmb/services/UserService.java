package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(Long clientId) {
        return userRepository.findAll().stream()
                .filter(us -> us.getClientId() == clientId)
                .findFirst()
                .orElse(null);
    }

    public void saveUser(Long chatId, Long clientId) {
        var user = new User(clientId, chatId);
        userRepository.save(user);
    }
}
