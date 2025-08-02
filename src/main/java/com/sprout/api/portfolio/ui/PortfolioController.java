package com.sprout.api.portfolio.ui;

import com.sprout.api.portfolio.application.PortfolioService;
import com.sprout.api.portfolio.application.result.PortfolioResult;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<PortfolioResult> getUserPortfolioByPeriod(
        @PathVariable Long userId,
        @RequestParam LocalDate from,
        @RequestParam LocalDate to,
        @RequestParam String regionCode
    ) {
        PortfolioResult result = portfolioService.getUserPortfolioByPeriod(userId, from, to, regionCode);
        return ResponseEntity.ok(result);
    }
}
