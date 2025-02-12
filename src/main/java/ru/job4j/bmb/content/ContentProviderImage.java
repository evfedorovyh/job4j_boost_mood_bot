package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import java.io.File;
import java.util.Random;

@Component
public class ContentProviderImage implements ContentProvider {
    private static final Random RND = new Random(System.currentTimeMillis());

    @Override
    public Content byMood(Long chatId, boolean goodMood) {
        var content = new Content(chatId);
        File[] files = new File("./files/images/").listFiles();
        if (files != null) {
            var index = RND.nextInt(0, files.length - 1);
            var imageFile = files[index];
            content.setPhoto(new InputFile(imageFile));
        }
        return content;
    }
}
