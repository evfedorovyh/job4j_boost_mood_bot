package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.telegram.TgUI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReminderService {
    private final MoodLogService moodLogService;
    private final TgUI tgUI;

    public ReminderService(MoodLogService moodLogService,
                           TgUI tgUI) {
        this.moodLogService = moodLogService;
        this.tgUI = tgUI;
    }

    public List<Content> reminderForAllUsers() {
        List<Content> listContent = new ArrayList<>();
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        for (var user : moodLogService.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
            var content = new Content(user.getChatId());
            content.setText("Как настроение?");
            content.setMarkup(tgUI.buildButtons());
            listContent.add(content);
        }
        return listContent;
    }
}
