package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import ru.job4j.bmb.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Component
public class ContentProviderText implements ContentProvider {
    private static final Random RND = new Random(System.currentTimeMillis());

    @Override
    public Content byMood(User user, boolean goodMood) {
        var content = new Content(user.getChatId());
        List<String> listStr = new ArrayList<>();
        try (Scanner sc = new Scanner(new File("./files/text/quotes.txt"))) {
            while (sc.hasNext()) {
                listStr.add(sc.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        var index = RND.nextInt(0, listStr.size() - 1);
        content.setText(listStr.get(index));
        return content;
    }
}
