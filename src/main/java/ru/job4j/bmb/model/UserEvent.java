package ru.job4j.bmb.model;

import org.springframework.context.ApplicationEvent;

public class UserEvent extends ApplicationEvent {
    private final User user;

    public UserEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
