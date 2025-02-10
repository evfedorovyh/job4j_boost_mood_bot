package ru.job4j.bmb.services;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.UserEvent;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.telegram.TelegramBotService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {
    private final MoodLogService moodLogService;
    private final AchievementRepository achievementRepository;
    private final AwardRepository awardRepository;
    private final TelegramBotService telegramBotService;

    public AchievementService(MoodLogService moodLogService,
                              AchievementRepository achievementRepository,
                              AwardRepository awardRepository,
                              TelegramBotService telegramBotService) {
        this.moodLogService = moodLogService;
        this.achievementRepository = achievementRepository;
        this.awardRepository = awardRepository;
        this.telegramBotService = telegramBotService;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
        var days = moodLogService.daysWithGoodMood(user);
        Achievement achievement = new Achievement();
        Content content = new Content(user.getChatId());
        long now = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        Optional<Award> awardIs = awardRepository.findAll().stream()
                .filter(award -> award.getDays() == days)
                .findAny();
        if (awardIs.isPresent()) {
            achievement.setAward(awardIs.get());
            achievement.setUser(user);
            achievement.setCreateAt(now);
            achievementRepository.save(achievement);
            content.setText(awardIs.get().getDescription());
            telegramBotService.send(content);
        }
    }
}
