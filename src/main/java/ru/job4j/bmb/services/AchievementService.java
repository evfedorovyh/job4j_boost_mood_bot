package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import static ru.job4j.bmb.MoodBotApplication.DATE_TIME_FORMATTER;

@Service
public class AchievementService {
    private final MoodLogService moodLogService;
    private final AchievementRepository achievementRepository;
    private final AwardRepository awardRepository;

    public AchievementService(MoodLogService moodLogService,
                              AchievementRepository achievementRepository,
                              AwardRepository awardRepository) {
        this.moodLogService = moodLogService;
        this.achievementRepository = achievementRepository;
        this.awardRepository = awardRepository;
    }

    public Optional<Content> setAchievement(User user) {
        var days = moodLogService.daysWithGoodMood(user);
        var achievement = new Achievement();
        var content = new Content(user.getChatId());
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
        }
        return Optional.of(content);
    }

    public Optional<Content> allAchievements(User user) {
        Content content = new Content(user.getChatId());
        List<Achievement> listAwards = achievementRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().equals(user))
                .toList();
        String stringLogs = formatAchievements(listAwards, "Все Ваши награды");
        content.setText(stringLogs);
        return Optional.of(content);
    }

    private String formatAchievements(List<Achievement> logs, String title) {
        if (logs.isEmpty()) {
            return String.format("%s:\nNo achievements found.", title);
        }
        var sb = new StringBuilder(title).append(":\n\n");
        logs.forEach(log -> {
            String formattedDate = DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(log.getCreateAt()));
            sb.append(formattedDate).append(": ")
                    .append(log.getAward().getTitle()).append("\n")
                    .append(log.getAward().getDescription()).append("\n\n");
        });
        return sb.toString();
    }
}
