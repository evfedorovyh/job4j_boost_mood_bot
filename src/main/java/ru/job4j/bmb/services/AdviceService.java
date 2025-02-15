package ru.job4j.bmb.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.*;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.model.UserAdvice;
import ru.job4j.bmb.repository.UserAdviceRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.job4j.bmb.MoodBotApplication.RND;

@Service
public class AdviceService {
    @Autowired
    private final List<ContentProvider> contents;
    private final UserAdviceRepository userAdviceRepository;
    private final MoodLogService moodLogService;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdviceService.class);

    public AdviceService(List<ContentProvider> contents,
                         UserAdviceRepository userAdviceRepository,
                         MoodLogService moodLogService) {
        this.contents = contents;
        this.userAdviceRepository = userAdviceRepository;
        this.moodLogService = moodLogService;
    }

    public Content adviceForUser(User user, boolean goodMood) {
        var index = RND.nextInt(0, contents.size());
        return contents.get(index).byMood(user, goodMood);
    }

    public void setAdvice(User user, boolean getAdvice) {
        userAdviceRepository.findAll().stream()
                .filter(a -> a.getUser().equals(user))
                .findFirst()
                .ifPresent(userAdviceRepository::delete);
        userAdviceRepository.save(new UserAdvice(user, getAdvice));
        LOGGER.info("Для пользователя {} \"Совет дня\" установлен {}", user.getClientId(), getAdvice);
    }

    public List<Content> advicesForAllUsers() {
        List<Content> listContent = new ArrayList<>();
        for (var userAdvice : userAdviceRepository.findAll()) {
            if (userAdvice.getAdvice()) {
                listContent.add(adviceForUser(userAdvice.getUser(),
                        moodLogService.getLastMood(userAdvice.getUser())));
            }
        }
        return listContent;
    }
}
