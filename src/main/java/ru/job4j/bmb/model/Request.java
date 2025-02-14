package ru.job4j.bmb.model;

import java.util.Objects;

public class Request {
    private  final  Long clientId;
    private  Long chatId;
    private  String command;
    private  Long moodId;

    public Request(Long clientId) {
        this.clientId = clientId;
    }

    public Request(Long clientId, Long chatId) {
        this.clientId = clientId;
        this.chatId = chatId;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getCommand() {
        return command;
    }

    public Long getMoodId() {
        return moodId;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setMoodId(Long moodId) {
        this.moodId = moodId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request request = (Request) o;
        return Objects.equals(clientId, request.clientId) && Objects.equals(command, request.command) && Objects.equals(moodId, request.moodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, command, moodId);
    }
}
