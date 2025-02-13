package com.capstone1.sasscapstone1.service.TagService;

import com.capstone1.sasscapstone1.entity.Tags;
import com.capstone1.sasscapstone1.repository.Tags.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagsRepository tagRepository;

    @Override
    public ResponseEntity<?> createTag(String tagName) {
        try {
            Tags tag = new Tags();
            tag.setTagName(tagName);
            tagRepository.save(tag);
            return ResponseEntity.ok(tag);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create tag");
        }
    }

    @Override
    public ResponseEntity<?> updateTag(Long tagId, String tagName) {
        Optional<Tags> tagOptional = tagRepository.findById(tagId);
        if (tagOptional.isPresent()) {
            Tags tag = tagOptional.get();
            tag.setTagName(tagName);
            tagRepository.save(tag);
            return ResponseEntity.ok(tag);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found");
        }
    }

    @Override
    public void deleteTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tag not found");
            return;
        }
        tagRepository.deleteById(tagId);
        ResponseEntity.ok("Tag deleted successfully");
    }


    @Override
    public List<Tags> getAllTags() {
        return tagRepository.findAll();
    }
}
