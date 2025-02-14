package ru.job4j.bmb.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.services.*;
import java.util.Optional;

@Service
public class BotCommandHandler {
    private final MoodService moodService;
    private final MoodLogService moodLogService;
    private final TgUI tgUI;
    private final AdviceService adviceService;
    private final UserService userService;
    private final AchievementService achievementService;

    public BotCommandHandler(MoodService moodService,
                             MoodLogService moodLogService,
                             TgUI tgUI,
                             AdviceService adviceService,
                             UserService userService,
                             AchievementService achievementService) {
        this.moodService = moodService;
        this.moodLogService = moodLogService;
        this.tgUI = tgUI;
        this.adviceService = adviceService;
        this.userService = userService;
        this.achievementService = achievementService;
    }

    public Optional<Content> handleCommands(Message message) {
        var content = new Content(message.getChatId());
        String textCommand = message.getText();
        var user = userService.findUser(message.getFrom().getId());
        if ("/start".equals(textCommand)) {
            if (user == null) {
                userService.saveUser(message.getFrom().getId(), message.getChatId());
                user = userService.findUser(message.getFrom().getId());
                adviceService.setAdvice(user, true);
            }
            content.setText("Как настроение?");
            content.setMarkup(tgUI.buildButtons());
            return Optional.of(content);
        } else if ("/week_mood_log".equals(textCommand)) {
           return moodLogService.weekMoodLogCommand(user);
        } else if ("/month_mood_log".equals(textCommand)) {
           return moodLogService.monthMoodLogCommand(user);
        } else if ("/award".equals(textCommand)) {
           return achievementService.allAchievements(user);
        } else if ("/daily_advice".equals(textCommand)) {
            return Optional.of(adviceService.adviceForUser(user, moodLogService.getLastMood(user)));
        } else if ("/on_daily_advice".equals(textCommand)) {
            adviceService.setAdvice(user, true);
            content.setText("Рассылка \"Совет дня\" включена");
            return Optional.of(content);
        } else if ("/off_daily_advice".equals(textCommand)) {
            adviceService.setAdvice(user, false);
            content.setText("Рассылка \"Совет дня\" отключена");
            return Optional.of(content);
        }
        return Optional.empty();
    }

    public Optional<Content> handleCallback(CallbackQuery callbackQuery) {
        var mood =  moodService.findMood(Long.valueOf(callbackQuery.getData()));
        var user =  userService.findUser(callbackQuery.getFrom().getId());
        return moodService.chooseMood(user, mood);
    }
}
