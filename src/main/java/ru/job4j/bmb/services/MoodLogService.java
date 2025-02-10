package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MoodLogService {
    UserRepository userRepository;
    MoodLogRepository moodLogRepository;

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

    public List<MoodLog> findByUserId(Long userId) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed());
    }

    public List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .collect(Collectors.toList());
    }

    public List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreatedAt() >= monthStart)
                .collect(Collectors.toList());
    }
}
