package ru.job4j.bmb.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.SendContent;
import ru.job4j.bmb.content.SendContentException;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SendContent {
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::send);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::send);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void send(Content content) {
        try {
            if (content.getAudio() != null) {
                var sendAudio = new SendAudio();
                sendAudio.setChatId(content.getChatId().toString());
                sendAudio.setAudio(content.getAudio());
                if (content.getText() != null) {
                    sendAudio.setTitle(content.getText());
                }
                execute(sendAudio);
            } else if (content.getText() != null && content.getMarkup() != null) {
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
}
