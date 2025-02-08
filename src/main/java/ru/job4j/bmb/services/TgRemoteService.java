package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;

@Service
public class TgRemoteService extends TelegramLongPollingBot {
    private final String botName;
    private final String botToken;
    private final UserRepository userRepository;
    private final MoodRepository moodRepository;
    private final MoodContentRepository moodContentRepository;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           UserRepository userRepository,
                           MoodRepository moodRepository,
                           MoodContentRepository moodContentRepository) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.moodRepository = moodRepository;
        this.moodContentRepository = moodContentRepository;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            send(new SendMessage(String.valueOf(chatId),
                    moodContentRepository.findById(Long.valueOf(data)).get().getText()));
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            if ("/start".equals(message.getText())) {
                long chatId = message.getChatId();
                var user = new User();
                user.setClientId(message.getFrom().getId());
                user.setChatId(chatId);
                if (!userRepository.findAll().contains(user)) {
                    userRepository.save(user);
                }
                send(sendButtons(chatId));
            } else {
                long chatId = update.getMessage().getChatId();
                send(sendButtons(chatId));
            }
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage sendButtons(long chatId) {
        TgUI tgUI = new TgUI(moodRepository);
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Как настроение сегодня?");
        inlineKeyboardMarkup = tgUI.buildButtons();
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
}
