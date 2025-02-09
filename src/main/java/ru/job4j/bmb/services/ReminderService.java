package ru.job4j.bmb.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.SendContent;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ReminderService {
    private final SendContent sendContent;
    private final MoodLogRepository moodLogRepository;
    private final TgUI tgUI;

    public ReminderService(SendContent sendContent,
                           MoodLogRepository moodLogRepository, TgUI tgUI) {
        this.sendContent = sendContent;
        this.moodLogRepository = moodLogRepository;
        this.tgUI = tgUI;
    }

    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void remindUsers() {
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        for (var user : moodLogRepository.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
            var content = new Content(user.getChatId());
            content.setText("Как настроение?");
            content.setMarkup(tgUI.buildButtons());
            sendContent.send(content);
        }
    }
}
