package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodFakeRepository;
import ru.job4j.bmb.repository.MoodLogFakeRepository;
import ru.job4j.bmb.repository.UserFakeRepository;
import ru.job4j.bmb.telegram.TgUI;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderServiceTest {
    @Test
    public void whenMoodGood() {
        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));
        var userRepository = new UserFakeRepository();
        var user = new User();
        user.setChatId(100);
        userRepository.save(user);
        var moodLogRepository = new MoodLogFakeRepository();
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        var moodLogService = new MoodLogService(moodLogRepository, userRepository);
        var result = new ReminderService(moodLogService, tgUI).reminderForAllUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }
}