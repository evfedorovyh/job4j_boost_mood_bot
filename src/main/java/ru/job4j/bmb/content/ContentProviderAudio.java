package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.job4j.bmb.model.User;

import java.io.File;

@Component
public class ContentProviderAudio implements ContentProvider {

    @Override
    public Content byMood(User user, boolean goodMood) {
        var content = new Content(user.getChatId());
        if (goodMood) {
            content.setAudio(new InputFile(new File("./files/audio/1.mp3")));
        } else {
            content.setAudio(new InputFile(new File("./files/audio/2.mp3")));
        }
        return content;
    }
}
