package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodLogService {
    UserRepository userRepository;
    MoodLogRepository moodLogRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodLogService(MoodLogRepository moodLogRepository,
                          UserRepository userRepository) {
        this.moodLogRepository = moodLogRepository;
        this.userRepository = userRepository;
    }

    public List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
        List<User> usersWhoDidNotVoted = userRepository.findAll();
        List<User> usersWhoVoted = moodLogRepository.findAll().stream()
                .filter(moodLog -> (moodLog.getCreatedAt() >= startOfDay && moodLog.getCreatedAt() <= endOfDay))
                .map(MoodLog::getUser)
                .distinct().toList();
        usersWhoDidNotVoted.removeAll(usersWhoVoted);
        return usersWhoDidNotVoted;
    }

    public int daysWithGoodMood(User user) {
        long now = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        int days = 0;
        Optional<MoodLog> firstMoodLog = moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().equals(user))
                .findFirst();
        Optional<MoodLog> lastMoodLog = moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().equals(user))
                .filter(moodLog -> !moodLog.getMood().isGood())
                .max((o1, o2) -> (int) (o1.getCreatedAt() - o2.getCreatedAt()));
        if (lastMoodLog.isPresent()) {
            days = (int) ((now - lastMoodLog.get().getCreatedAt()) / 86400000 - 1);
        } else if (firstMoodLog.isPresent()) {
            days = (int) (now - firstMoodLog.get().getCreatedAt()) / 86400000;
        }
        return days;
    }

    public boolean getLastMood(User user) {
        Optional<MoodLog> lastMoodLog = moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().equals(user))
                .max((o1, o2) -> (int) (o1.getCreatedAt() - o2.getCreatedAt()));
        return lastMoodLog.map(moodLog -> moodLog.getMood().isGood()).orElse(true);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        Content content = new Content(chatId);
        long weekStart = LocalDate.now()
                .minusWeeks(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        List<MoodLog> listMoodLog = moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == clientId)
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .toList();
        String stringLogs = formatMoodLogs(listMoodLog, "Лог настроений за неделю");
        content.setText(stringLogs);
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        Content content = new Content(chatId);
        long weekStart = LocalDate.now()
                .minusMonths(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        List<MoodLog> listMoodLog = moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == clientId)
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .toList();
        String stringLogs = formatMoodLogs(listMoodLog, "Лог настроений за месяц");
        content.setText(stringLogs);
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochMilli(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n\n");
        });
        return sb.toString();
    }
}
