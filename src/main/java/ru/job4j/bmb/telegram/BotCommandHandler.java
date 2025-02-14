package ru.job4j.bmb.telegram;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Request;
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

    public Optional<Content> handleCommands(Request request) {
        var content = new Content(request.getClientId());
        String textCommand = request.getCommand();
        var user = userService.findUser(request.getClientId());
        if ("/start".equals(textCommand)) {
            if (user == null) {
                userService.saveUser(request.getClientId(), request.getChatId());
                user = userService.findUser(request.getClientId());
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

    public Optional<Content> handleCallback(Request request) {
        var mood =  moodService.findMood(request.getMoodId());
        var user =  userService.findUser(request.getClientId());
        return moodService.chooseMood(user, mood);
    }
}
