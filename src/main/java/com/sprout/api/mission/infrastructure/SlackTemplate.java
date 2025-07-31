package com.sprout.api.mission.infrastructure;

import com.slack.api.Slack;
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

    public String send(String payload) {
        //todo: 알림 처리하기
        return null;
    }
}
