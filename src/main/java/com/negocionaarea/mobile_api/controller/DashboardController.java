package com.negocionaarea.mobile_api.controller;

import com.negocionaarea.mobile_api.dto.DashboardAnalyticsDto;
import com.negocionaarea.mobile_api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ENTERPRISE')")
    public ResponseEntity<DashboardAnalyticsDto> getAnalytics(
            @RequestParam(defaultValue = "mes") String periodo,
            JwtAuthenticationToken auth
    ) {
        String emailEmpresa = auth.getToken().getSubject();
        DashboardAnalyticsDto analytics = dashboardService.gerarAnalytics(emailEmpresa, periodo);
        return ResponseEntity.ok(analytics);
    }
}
