package com.capstone1.sasscapstone1.controller.TagController;

import com.capstone1.sasscapstone1.entity.Tags;
import com.capstone1.sasscapstone1.service.TagService.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestParam String tagName) {
        ResponseEntity<?> tag = tagService.createTag(tagName);
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/update/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable Long tagId, @RequestParam String tagName) {
        ResponseEntity<?> updatedTag = tagService.updateTag(tagId, tagName);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/delete/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok("Tag deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tags>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
