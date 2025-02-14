package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.job4j.bmb.model.User;

import java.io.File;
import java.util.Random;

@Component
public class ContentProviderImage implements ContentProvider {
    private static final Random RND = new Random(System.currentTimeMillis());

    @Override
    public Content byMood(User user, boolean goodMood) {
        var content = new Content(user.getChatId());
        File[] files = new File("./files/images/").listFiles();
        if (files != null) {
            var index = RND.nextInt(0, files.length - 1);
            var imageFile = files[index];
            content.setPhoto(new InputFile(imageFile));
        }
        return content;
    }
}
