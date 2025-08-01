package com.sprout.api.mission.infrastructure;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlackTemplate {

    private final Slack slack;
    private final String webhookUrl;

    public SlackTemplate(@Value("${app.notification.web-hook.url}") final String webhookUrl) {
        this.slack = new Slack();
        this.webhookUrl = webhookUrl;
    }

    public void sendSuccess(String text) {
        Payload payload = Payload.builder().text(text).build();
        send(payload);
    }

    public void sendFail(String text) {
        Payload payload = Payload.builder().text(text).build();
        send(payload);
    }

    public void send(Payload payload) {
        try {
            slack.send(webhookUrl, payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
