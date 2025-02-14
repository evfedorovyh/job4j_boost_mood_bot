package ru.job4j.bmb.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.*;
import ru.job4j.bmb.telegram.TgUI;
import java.time.LocalDate;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {UserFakeRepository.class, MoodLogFakeRepository.class,
        MoodFakeRepository.class, ReminderService.class, TgUI.class, MoodLogService.class})
class ReminderServiceTest {
    @Autowired
    @Qualifier("userFakeRepository")
    UserRepository userRepository;
    @Autowired
    @Qualifier("moodLogFakeRepository")
    MoodLogRepository moodLogRepository;
    @Autowired
    @Qualifier("moodFakeRepository")
    MoodRepository moodRepository;
    @Autowired
    ReminderService reminderService;

    @Test
    public void whenMoodGood() {
        moodRepository.save(new Mood("Good", true));
        var user = new User();
        user.setChatId(100);
        userRepository.save(user);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var result = reminderService.reminderForAllUsers();
        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }
}