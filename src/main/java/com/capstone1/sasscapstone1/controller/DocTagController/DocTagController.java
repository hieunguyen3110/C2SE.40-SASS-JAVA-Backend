package com.capstone1.sasscapstone1.controller.DocTagController;

import com.capstone1.sasscapstone1.entity.DocTag;
import com.capstone1.sasscapstone1.service.DocTagService.DocTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/document-tags")
public class DocTagController {

    @Autowired
    private DocTagService docTagService;

    @PostMapping("/add")
    public ResponseEntity<?> addTagToDocument(@RequestParam Long documentId, @RequestParam Long tagId) {
        // Gọi service để thêm tag vào document
        return docTagService.addTagToDocument(documentId, tagId);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeTagFromDocument(@RequestParam Long documentId, @RequestParam Long tagId) {
        docTagService.removeTagFromDocument(documentId, tagId);
        return ResponseEntity.ok("Tag removed from document successfully");
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<List<DocTag>> getTagsByDocument(@PathVariable Long documentId) {
        return docTagService.getTagsByDocument(documentId);
    }
}
