package ru.job4j.bmb.telegram;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.GetRequest;
import ru.job4j.bmb.content.SendContent;
import ru.job4j.bmb.content.SendContentException;
import ru.job4j.bmb.model.Request;
import ru.job4j.bmb.model.UserEvent;
import ru.job4j.bmb.services.AchievementService;
import ru.job4j.bmb.services.AdviceService;
import ru.job4j.bmb.services.ReminderService;
import java.util.List;

@Service
@Conditional(OnFakeCondition.class)
public class TelegramBotServiceFake implements SendContent, GetRequest, ApplicationListener<UserEvent> {
    private final BotCommandHandler handler;
    private final AdviceService adviceService;
    private final ReminderService reminderService;
    private final AchievementService achievementService;

    public TelegramBotServiceFake(BotCommandHandler handler,
                                  AdviceService adviceService,
                                  ReminderService reminderService,
                                  AchievementService achievementService) {
        this.handler = handler;
        this.adviceService = adviceService;
        this.reminderService = reminderService;
        this.achievementService = achievementService;
    }

    @Override
    public void getRequest(Request request) {
        if (request.getMoodId() != null) {
            handler.handleCallback(request)
                    .ifPresent(this::sendContent);
        } else if (request.getCommand() != null) {
            handler.handleCommands(request)
                    .ifPresent(this::sendContent);
        }
    }

    @Override
    public void sendContent(Content content) {
        try {
            if (content.getText() != null) {
                System.out.println(content.getText());
            }
        } catch (SendContentException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        achievementService.setAchievement(event.getUser())
                .ifPresent(this::sendContent);
    }

    @Scheduled(cron = "${send.advice.time}", zone = "Europe/Moscow")
    public void sendAdvice() {
        List<Content> listContent = adviceService.advicesForAllUsers();
        for (var content : listContent) {
            sendContent(content);
        }
    }

    @Scheduled(cron = "${send.remainder.time}", zone = "Europe/Moscow")
    public void sendRemainder() {
        List<Content> listContent = reminderService.reminderForAllUsers();
        for (var content : listContent) {
            sendContent(content);
        }
    }
}