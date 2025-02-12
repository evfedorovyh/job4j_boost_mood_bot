package ru.job4j.bmb.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.services.MoodLogService;
import ru.job4j.bmb.services.MoodService;
import ru.job4j.bmb.services.RecommendationEngine;
import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final MoodLogService moodLogService;
    private final TgUI tgUI;
    private final RecommendationEngine recommendationEngine;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI,
                             RecommendationEngine recommendationEngine,
                             MoodLogService moodLogService) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
        this.recommendationEngine = recommendationEngine;
        this.moodLogService = moodLogService;
    }

    public Optional<Content> commands(Message message) {
        if ("/start".equals(message.getText())) {
           return handleStartCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/week_mood_log".equals(message.getText())) {
           return moodLogService.weekMoodLogCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/month_mood_log".equals(message.getText())) {
           return moodLogService.monthMoodLogCommand(message.getChatId(), message.getFrom().getId());
        } else if ("/award".equals(message.getText())) {
           return moodService.allAchievements(message.getChatId(), message.getFrom().getId());
        } else if ("/daily_advice".equals(message.getText())) {
            var user = userRepository.findAll().stream()
                    .filter(us -> us.getClientId() == message.getFrom().getId()).findFirst().get();
            return Optional.of(recommendationEngine.adviceForUser(message.getChatId(), moodLogService.getLastMood(user)));
        } else if ("/on_daily_advice".equals(message.getText())) {
            var user = userRepository.findAll().stream()
                    .filter(us -> us.getClientId() == message.getFrom().getId()).findFirst().get();
            recommendationEngine.setAdvice(user, true);
            var content = new Content(message.getChatId());
            content.setText("Рассылка \"Совет дня\" включена");
            return Optional.of(content);
        } else if ("/off_daily_advice".equals(message.getText())) {
            var user = userRepository.findAll().stream()
                    .filter(us -> us.getClientId() == message.getFrom().getId()).findFirst().get();
            recommendationEngine.setAdvice(user, false);
            var content = new Content(message.getChatId());
            content.setText("Рассылка \"Совет дня\" отключена");
            return Optional.of(content);
        }
        return Optional.empty();
    }

    public Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findAll().stream().filter(user1 -> user1.getClientId() == callback.getFrom().getId()).findFirst();
        return user.map(value -> moodService.chooseMood(value, moodId));
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        if (!userRepository.findAll().contains(user)) {
            userRepository.save(user);
            recommendationEngine.setAdvice(user, true);
        }
        var content = new Content(chatId);
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }
}
