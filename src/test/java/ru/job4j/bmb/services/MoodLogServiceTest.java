package ru.job4j.bmb.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogFakeRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserFakeRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {User.class, MoodLogFakeRepository.class, UserFakeRepository.class, MoodLogService.class})
class MoodLogServiceTest {
    @Autowired
    @Qualifier("userFakeRepository")
    UserRepository userRepository;
    @Autowired
    @Qualifier("moodLogFakeRepository")
    MoodLogRepository moodLogRepository;
    @Autowired
    MoodLogService moodLogService;

    @Test
    public void whenUserFound() {
        userRepository.save(new User(1L, 100, 100));
        userRepository.save(new User(2L, 200, 200));
        userRepository.save(new User(3L, 300, 300));
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLogRepository.save(new MoodLog(1L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(10).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(2L, new User(2L, 200, 200),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(1).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(3L, new User(3L, 300, 300),
                new Mood("good", true),
                LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                        .plusHours(2)
                        .toInstant()
                        .toEpochMilli()));
        List<User> users = moodLogService.findUsersWhoDidNotVoteToday(startOfDay, endOfDay);
        List<User> expected = List.of(
                new User(1L, 100, 100),
                new User(2L, 200, 200));
        assertTrue(users.containsAll(expected));
    }

    @Test
    public void daysWithGoodMoodIs3() {
        User userOne = new User(1L, 100, 100);
        moodLogRepository.save(new MoodLog(1L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(5).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(2L, new User(1L, 100, 100),
                new Mood("good", false),
                LocalDateTime.now()
                        .minusDays(4).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(3L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(3).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(4L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(2).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(5L, new User(2L, 200, 200),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(1).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        int days = moodLogService.daysWithGoodMood(userOne);
        assertThat(days).isEqualTo(3);
    }

    @Test
    public void daysWithGoodMoodIs10() {
        User userTwo = new User(2L, 200, 200);
        moodLogRepository.save(new MoodLog(1L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(5).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(2L, new User(1L, 100, 100),
                new Mood("good", false),
                LocalDateTime.now()
                        .minusDays(4).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(3L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(3).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(4L, new User(1L, 100, 100),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(2).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        moodLogRepository.save(new MoodLog(5L, new User(2L, 200, 200),
                new Mood("good", true),
                LocalDateTime.now()
                        .minusDays(10).atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli()));
        int days = moodLogService.daysWithGoodMood(userTwo);
        assertThat(days).isEqualTo(10);
    }
}