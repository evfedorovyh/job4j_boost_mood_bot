package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.telegram.TelegramBotService;

@Service
public class ReminderService implements BeanNameAware {
    private final TelegramBotService telegramBotService;
    private final UserRepository userRepository;

    public ReminderService(TelegramBotService telegramBotService, UserRepository userRepository) {
        this.telegramBotService = telegramBotService;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRateString = "${remind.period}")
    public void ping() {
        for (var user : userRepository.findAll()) {
            var content = new Content(user.getChatId());
            content.setText("Ping");
            telegramBotService.send(content);
        }
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println(beanName);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed via @PreDestroy.");
    }
}
