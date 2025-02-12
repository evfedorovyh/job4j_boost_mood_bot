package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Component
public class ContentProviderAudio implements ContentProvider {

    @Override
    public Content byMood(Long chatId, boolean goodMood) {
        var content = new Content(chatId);
        if (goodMood) {
            content.setAudio(new InputFile(new File("./files/audio/1.mp3")));
        } else {
            content.setAudio(new InputFile(new File("./files/audio/2.mp3")));
        }
        return content;
    }
}
