package ru.job4j.bmb.content;

import ru.job4j.bmb.model.User;

public interface ContentProvider {

    Content byMood(User user, boolean goodMood);
}
