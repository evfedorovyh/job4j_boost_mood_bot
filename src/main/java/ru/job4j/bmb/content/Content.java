package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class Content {
    private final Long chatId;
    private String text;
    private InputFile photo;
    private InlineKeyboardMarkup markup;
    private InputFile audio;
    private InputFile video;

    public Content(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public InputFile getPhoto() {
        return photo;
    }

    public InlineKeyboardMarkup getMarkup() {
        return markup;
    }

    public InputFile getAudio() {
        return audio;
    }

    public InputFile getVideo() {
        return video;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPhoto(InputFile photo) {
        this.photo = photo;
    }

    public void setMarkup(InlineKeyboardMarkup markup) {
        this.markup = markup;
    }

    public void setAudio(InputFile audio) {
        this.audio = audio;
    }

    public void setVideo(InputFile video) {
        this.video = video;
    }
}
