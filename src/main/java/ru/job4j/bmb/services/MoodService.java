package ru.job4j.bmb.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodContentService moodContentService;
    private final MoodLogService moodLogService;
    private final MoodRepository moodRepository;
    private final ApplicationEventPublisher publisher;
    public final AchievementRepository achievementRepository;

    public MoodService(MoodContentService moodContentService,
                       MoodLogService moodLogService,
                       MoodRepository moodRepository,
                       ApplicationEventPublisher publisher,
                       AchievementRepository achievementRepository) {
        this.moodContentService = moodContentService;
        this.moodLogService = moodLogService;
        this.moodRepository = moodRepository;
        this.publisher = publisher;
        this.achievementRepository = achievementRepository;
    }

    public Mood findMood(Long moodId) {
        return  moodRepository.findAll().get((moodId.intValue() - 1));
    }

    public Optional<Content> chooseMood(User user, Mood mood) {
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        moodLog.setMood(mood);
        moodLog.setCreatedAt(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        moodLogService.saveMoodLog(moodLog);
        publisher.publishEvent(new UserEvent(this, user));
        Content content = new Content(user.getChatId());
        content.setText(moodContentService.findMoodContent(mood).getText());
        return Optional.of(content);
    }
}
