package com.capstone1.sasscapstone1.service.DocTagService;

import com.capstone1.sasscapstone1.entity.DocTag;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DocTagService {
    ResponseEntity<DocTag> addTagToDocument(Long documentId, Long tagId);
    ResponseEntity<String> removeTagFromDocument(Long documentId, Long tagId);
    ResponseEntity<List<DocTag>> getTagsByDocument(Long documentId);
}
