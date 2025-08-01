package com.sprout.api.mission.infrastructure;

import com.slack.api.Slack;
import com.slack.api.model.block.ContextBlock;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.webhook.Payload;
import com.sprout.api.mission.application.result.BatchResult;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
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

    public void sendBatchStart(int totalRegions) {
        Payload payload = Payload.builder()
            .text("🚀 미션 배치 작업 시작")
            .blocks(List.of(
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text("*🚀 미션 배치 작업 시작*\n" +
                            "• 총 처리 대상: " + totalRegions + "개 지역\n" +
                            "• 예상 소요 시간: " + ((totalRegions * 4) / 60 + 1) + "분")
                        .build())
                    .build(),
                DividerBlock.builder().build()
            ))
            .build();

        this.send(payload);
    }

    public void sendBatchProgress(BatchResult result) {
        int totalProcessed = result.getSuccessCount() + result.getFailCount();
        double progressPercentage = (double) totalProcessed / result.getTotalExpected() * 100;

        // 25%, 50%, 75% 시점에서만 전송
        if (progressPercentage != 25.0 && progressPercentage != 50.0 && progressPercentage != 75.0) {
            return;
        }

        String emoji = getProgressEmoji((int)progressPercentage);
        String progressBar = getProgressBar((int)progressPercentage);

        Payload payload = Payload.builder()
            .text(emoji + " 미션 배치 진행률 " + (int)progressPercentage + "%")
            .blocks(List.of(
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text("*" + emoji + " 미션 배치 진행률 " + (int)progressPercentage + "%*\n" +
                            progressBar)
                        .build())
                    .build(),

                SectionBlock.builder()
                    .fields(List.of(
                        MarkdownTextObject.builder()
                            .text("*✅ 완료 지역*\n" + result.getSuccessCount() + "개")
                            .build(),
                        MarkdownTextObject.builder()
                            .text("*❌ 실패 지역*\n" + result.getFailCount() + "개")
                            .build()
                    ))
                    .build(),

                buildRegionListSection("✅ 미션 생성 완료 지역", result.getSuccessRegions(), "good"),
                buildRegionListSection("❌ 미션 생성 실패 지역", result.getFailRegions(), "danger"),

                DividerBlock.builder().build()
            ))
            .build();

        this.send(payload);
    }

    public void sendBatchComplete(BatchResult result) {
        String emoji = result.getFailCount() == 0 ? "🎉" : "⚠️";
        String status = result.getFailCount() == 0 ? "완료" : "부분 완료";

        Payload payload = Payload.builder()
            .text(emoji + " 미션 배치 작업 " + status)
            .blocks(List.of(
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text("*" + emoji + " 미션 배치 작업 " + status + "*\n" +
                            getProgressBar(100))
                        .build())
                    .build(),

                SectionBlock.builder()
                    .fields(List.of(
                        MarkdownTextObject.builder()
                            .text("*📊 전체 결과*\n" +
                                "• 성공: " + result.getSuccessCount() + "개\n" +
                                "• 실패: " + result.getFailCount() + "개\n" +
                                "• 성공률: " + String.format("%.1f", result.getSuccessRate()) + "%")
                            .build(),
                        MarkdownTextObject.builder()
                            .text("*⏱️ 소요 시간*\n" + formatDuration(result.getDuration()))
                            .build()
                    ))
                    .build(),

                buildRegionListSection("✅ 미션 생성 완료 지역", result.getSuccessRegions(), "good"),
                buildRegionListSection("❌ 미션 생성 실패 지역", result.getFailRegions(), "danger")
            ))
            .build();

        this.send(payload);
    }

    public void sendBatchError(String errorMessage) {
        Payload payload = Payload.builder()
            .text("🚨 미션 배치 시스템 오류")
            .blocks(List.of(
                SectionBlock.builder()
                    .text(MarkdownTextObject.builder()
                        .text("*🚨 미션 배치 시스템 오류*\n" +
                            "```" + errorMessage + "```")
                        .build())
                    .build(),

                ContextBlock.builder()
                    .elements(List.of(
                        MarkdownTextObject.builder()
                            .text("⚠️ 시스템 레벨 오류로 배치 작업이 중단되었습니다. 개발팀 확인이 필요합니다.")
                            .build()
                    ))
                    .build()
            ))
            .build();

        this.send(payload);
    }

    // Helper methods
    private SectionBlock buildRegionListSection(String title, List<String> regions, String color) {
        if (regions.isEmpty()) {
            return SectionBlock.builder()
                .text(MarkdownTextObject.builder()
                    .text("*" + title + "*\n없음")
                    .build())
                .build();
        }

        String regionList = regions.size() <= 10
            ? String.join(", ", regions)
            : String.join(", ", regions.subList(0, 10)) + " 외 " + (regions.size() - 10) + "개";

        return SectionBlock.builder()
            .text(MarkdownTextObject.builder()
                .text("*" + title + "*\n" + regionList)
                .build())
            .build();
    }

    private String getProgressEmoji(int percentage) {
        return switch (percentage) {
            case 25 -> "🌱";
            case 50 -> "🌿";
            case 75 -> "🌳";
            default -> "🎉";
        };
    }

    private String getProgressBar(int percentage) {
        int filled = percentage / 10;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        return bar + " " + percentage + "%";
    }

    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        return minutes + "분 " + seconds + "초";
    }

    public void send(Payload payload) {
        try {
            slack.send(webhookUrl, payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}