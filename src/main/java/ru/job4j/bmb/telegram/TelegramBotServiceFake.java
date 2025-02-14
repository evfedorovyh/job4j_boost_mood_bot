package ru.job4j.bmb.telegram;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.GetRequest;
import ru.job4j.bmb.content.SendContent;
import ru.job4j.bmb.content.SendContentException;
import ru.job4j.bmb.model.Request;

@Service
@Conditional(OnFakeCondition.class)
public class TelegramBotServiceFake implements SendContent, GetRequest {
    private final BotCommandHandler handler;

    public TelegramBotServiceFake(BotCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void getRequest(Request request) {
        if (request.getMoodId() != null) {
            handler.handleCallback(request)
                    .ifPresent(this::sendContent);
        } else if (request.getCommand() != null) {
            handler.handleCommands(request)
                    .ifPresent(this::sendContent);
        }
    }

    @Override
    public void sendContent(Content content) {
        try {
            if (content.getText() != null) {
                System.out.println(content.getText());
            }
        } catch (SendContentException e) {
            e.printStackTrace();
        }
    }
}
