package ru.job4j.bmb.content;

public class SendContentException extends RuntimeException {
    public SendContentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
