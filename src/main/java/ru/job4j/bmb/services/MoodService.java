package ru.job4j.bmb.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.MoodContentRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodContentRepository moodContentRepository;
    private final MoodRepository moodRepository;
    private final MoodLogRepository moodLogRepository;
    private final ApplicationEventPublisher publisher;
    public final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodContentRepository moodContentRepository,
                       MoodRepository moodRepository,
                       MoodLogRepository moodLogRepository,
                       ApplicationEventPublisher publisher,
                       AchievementRepository achievementRepository) {
        this.moodLogRepository = moodLogRepository;
        this.publisher = publisher;
        this.moodRepository = moodRepository;
        this.moodContentRepository = moodContentRepository;
        this.achievementRepository = achievementRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        MoodLog moodLog = new MoodLog();
        moodLog.setUser(user);
        moodLog.setMood(moodRepository.findAll().get(moodId.intValue() - 1));
        moodLog.setCreatedAt(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        moodLogRepository.save(moodLog);
        publisher.publishEvent(new UserEvent(this, user));
        Content content = new Content(user.getChatId());
        content.setText(moodContentRepository.findAll().get(moodId.intValue() - 1).getText());
        return content;
    }

    public Optional<Content> allAchievements(long chatId, Long clientId) {
        Content content = new Content(chatId);
        List<Achievement> listAwards = achievementRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == clientId)
                .toList();
        String stringLogs = formatAchievements(listAwards, "Все Ваши награды");
        content.setText(stringLogs);
        return Optional.of(content);
    }

    private String formatAchievements(List<Achievement> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo achievements found.";
        }
        var sb = new StringBuilder(title + ":\n\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochMilli(log.getCreateAt()));
            sb.append(formattedDate).append(": ")
                    .append(log.getAward().getTitle())
                    .append("\n")
                    .append(log.getAward().getDescription())
                    .append("\n\n");
        });
        return sb.toString();
    }
}
