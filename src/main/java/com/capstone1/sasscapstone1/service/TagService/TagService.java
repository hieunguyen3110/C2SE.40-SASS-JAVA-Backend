package com.capstone1.sasscapstone1.service.TagService;

import com.capstone1.sasscapstone1.entity.Tags;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TagService {
    ResponseEntity<?> createTag(String tagName);
    ResponseEntity<?>  updateTag(Long tagId, String tagName);
    void deleteTag(Long tagId);
    List<Tags> getAllTags();
}
