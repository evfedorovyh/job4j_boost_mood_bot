package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.*;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.model.UserAdvice;
import ru.job4j.bmb.repository.UserAdviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RecommendationEngine {
    private final List<ContentProvider> contents;
    private final UserAdviceRepository userAdviceRepository;
    private final MoodLogService moodLogService;
    private static final Random RND = new Random(System.currentTimeMillis());

    public RecommendationEngine(List<ContentProvider> contents,
                                UserAdviceRepository userAdviceRepository,
                                MoodLogService moodLogService) {
        this.contents = contents;
        this.userAdviceRepository = userAdviceRepository;
        this.moodLogService = moodLogService;
    }

    public Content adviceForUser(Long chatId, boolean goodMood) {
        contents.add(new ContentProviderText());
        contents.add(new ContentProviderAudio());
        contents.add(new ContentProviderImage());
        var index = RND.nextInt(0, contents.size());
        return contents.get(index).byMood(chatId, goodMood);
    }

    public void setAdvice(User user, boolean getAdvice) {
        userAdviceRepository.findAll().stream()
                .filter(a -> a.getUser().equals(user))
                .findFirst()
                .ifPresent(userAdviceRepository::delete);
        userAdviceRepository.save(new UserAdvice(user, getAdvice));
        System.out.println("Для пользователя " + user.getClientId() + " \"Совет дня\" установлен " + getAdvice);
    }

    public List<Content> advicesForAllUsers() {
        List<Content> listContent = new ArrayList<>();
        for (var userAdvice : userAdviceRepository.findAll()) {
            if (userAdvice.getAdvice()) {
                listContent.add(adviceForUser(userAdvice.getUser().getChatId(),
                        moodLogService.getLastMood(userAdvice.getUser())));
            }
        }
        return listContent;
    }
}
