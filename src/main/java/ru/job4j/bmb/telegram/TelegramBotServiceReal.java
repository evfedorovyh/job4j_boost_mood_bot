package ru.job4j.bmb.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.SendContent;
import ru.job4j.bmb.content.SendContentException;
import ru.job4j.bmb.model.UserEvent;
import ru.job4j.bmb.services.AchievementService;
import ru.job4j.bmb.services.AdviceService;
import ru.job4j.bmb.services.ReminderService;
import java.util.List;

@Service
@Conditional(OnRealCondition.class)
public class TelegramBotServiceReal extends TelegramLongPollingBot
        implements SendContent, ApplicationListener<UserEvent> {
    private final BotCommandHandler handler;
    private final String botName;
    private final AdviceService adviceService;
    private final ReminderService reminderService;
    private final AchievementService achievementService;

    public TelegramBotServiceReal(@Value("${telegram.bot.name}") String botName,
                                  @Value("${telegram.bot.token}") String botToken,
                                  BotCommandHandler handler,
                                  AdviceService adviceService,
                                  ReminderService reminderService,
                                  AchievementService achievementService) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
        this.adviceService = adviceService;
        this.reminderService = reminderService;
        this.achievementService = achievementService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sendContent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.handleCommands(update.getMessage())
                    .ifPresent(this::sendContent);
        }
    }

    @Override
    public void sendContent(Content content) {
        try {
            if (content.getAudio() != null) {
                var sendAudio = new SendAudio();
                sendAudio.setChatId(content.getChatId().toString());
                sendAudio.setAudio(content.getAudio());
                if (content.getText() != null) {
                    sendAudio.setTitle(content.getText());
                }
                execute(sendAudio);
            } else if (content.getText() != null
                    && content.getMarkup() != null) {
                var sendMessage = new SendMessage();
                sendMessage.setChatId(content.getChatId().toString());
                sendMessage.setText(content.getText());
                sendMessage.setReplyMarkup(content.getMarkup());
                execute(sendMessage);
            } else if (content.getText() != null) {
                var sendMessage = new SendMessage();
                sendMessage.setChatId(content.getChatId().toString());
                sendMessage.setText(content.getText());
                execute(sendMessage);
            } else if (content.getPhoto() != null) {
                var sendPhoto = new SendPhoto();
                sendPhoto.setChatId(content.getChatId().toString());
                sendPhoto.setPhoto(content.getPhoto());
                execute(sendPhoto);
            }
        } catch (SendContentException | TelegramApiException e) {
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

    @Override
    public String getBotUsername() {
        return botName;
    }
}