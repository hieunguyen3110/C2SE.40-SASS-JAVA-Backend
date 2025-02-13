package com.capstone1.sasscapstone1.service.DocTagService;

import com.capstone1.sasscapstone1.entity.DocTag;
import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.entity.Tags;
import com.capstone1.sasscapstone1.exception.DocumentException;
import com.capstone1.sasscapstone1.exception.TagException;
import com.capstone1.sasscapstone1.repository.Tags.TagsRepository;
import com.capstone1.sasscapstone1.repository.DocTag.DocTagRepository;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.service.DocTagService.DocTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocTagServiceImpl implements DocTagService {

    @Autowired
    private DocTagRepository docTagRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private TagsRepository tagRepository;

    @Override
    public ResponseEntity<DocTag> addTagToDocument(Long documentId, Long tagId) {
        // Tìm document theo ID
        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Document not found"));

        // Tìm tag theo ID
        Tags tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagException("Tag not found"));

        // Tạo DocTag và lưu vào cơ sở dữ liệu
        DocTag docTag = new DocTag();
        docTag.setDocuments(document);
        docTag.setTags(tag);
        DocTag savedDocTag = docTagRepository.save(docTag);

        // Trả về ResponseEntity với mã trạng thái HTTP 201 CREATED
        return new ResponseEntity<>(savedDocTag, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> removeTagFromDocument(Long documentId, Long tagId) {
        // Tìm mối quan hệ giữa document và tag
        DocTag docTag = docTagRepository.findByDocuments_DocIdAndTags_TagId(documentId, tagId)
                .orElseThrow(() -> new DocumentException("Document-Tag relation not found"));

        // Xóa mối quan hệ khỏi cơ sở dữ liệu
        docTagRepository.delete(docTag);

        // Trả về ResponseEntity với mã trạng thái HTTP 200 OK
        return ResponseEntity.ok("Tag removed from document successfully");
    }

    @Override
    public ResponseEntity<List<DocTag>> getTagsByDocument(Long documentId) {
        // Tìm tất cả các tag liên quan đến document
        List<DocTag> docTags = docTagRepository.findByDocuments_DocId(documentId);

        // Trả về ResponseEntity với danh sách tag
        return ResponseEntity.ok(docTags);
    }
}
