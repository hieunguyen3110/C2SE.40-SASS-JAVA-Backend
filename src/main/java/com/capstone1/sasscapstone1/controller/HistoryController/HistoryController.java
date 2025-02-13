package com.capstone1.sasscapstone1.controller.HistoryController;

import com.capstone1.sasscapstone1.dto.FolderDownloadStatsDto.FolderDownloadStatsDto;
import com.capstone1.sasscapstone1.dto.PopularDocumentDto.PopularDocumentDto;
import com.capstone1.sasscapstone1.entity.History;
import com.capstone1.sasscapstone1.service.HistoryService.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    // Track document download
    @PostMapping("/track")
    public ResponseEntity<String> trackDownload(@RequestParam Long docId, @RequestParam String username) {
        historyService.trackDownload(docId, username);
        return ResponseEntity.ok("Download tracked successfully");
    }
}


