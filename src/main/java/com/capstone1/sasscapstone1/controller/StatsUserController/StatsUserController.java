package com.capstone1.sasscapstone1.controller.StatsUserController;


import com.capstone1.sasscapstone1.dto.AdminDashboardStatsDto.StatsDto;
import com.capstone1.sasscapstone1.service.StatsUserService.StatsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class StatsUserController {

    private final StatsUserService statsUserService;

    @Autowired
    public StatsUserController(StatsUserService statsUserService) {
        this.statsUserService = statsUserService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsDto> getDashboardStats() {
        StatsDto stats = statsUserService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
