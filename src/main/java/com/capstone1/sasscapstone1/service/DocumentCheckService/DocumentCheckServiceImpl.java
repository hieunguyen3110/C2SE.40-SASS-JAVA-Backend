package com.capstone1.sasscapstone1.service.DocumentCheckService;

import com.capstone1.sasscapstone1.dto.ChatbotDTO.ChatbotResponse;
import com.capstone1.sasscapstone1.dto.CheckFileResponse.CheckFileResponse;
import com.capstone1.sasscapstone1.entity.Documents;
import com.capstone1.sasscapstone1.exception.DocumentException;
import com.capstone1.sasscapstone1.repository.Documents.DocumentsRepository;
import com.capstone1.sasscapstone1.request.SendMessageRequest;
import com.capstone1.sasscapstone1.service.FileService.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentCheckServiceImpl implements DocumentCheckService {

    private final DocumentsRepository documentsRepository;
    private final RestTemplate restTemplate;

    @Override
    public void checkDocument(Long docId) {
        try {
            // Lấy tài liệu từ database
            Documents document = documentsRepository.findById(docId)
                    .orElseThrow(() -> new DocumentException("Document not found with ID: " + docId));
            if(document.getIsCheck()){
                throw new Exception("Tài liệu đã được check");
            }
            // Đọc nội dung file
            HttpHeaders httpHeaders= new HttpHeaders();
            Map<String,String> request= new HashMap<>();
            request.put("filePath",document.getFilePath());
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Map<String,String>> entity= new HttpEntity<>(request,httpHeaders);
            String uri = "http://127.0.0.1:5000/api/check-file";
            CheckFileResponse result= restTemplate.postForEntity(uri,entity,CheckFileResponse.class).getBody();
            assert result != null;
            if(result.getSensitiveWords()!=null && result.getSensitiveWords().size()>10){
                throw new Exception("File chứa từ nhạy cảm quá nhiều!");
            }
            document.setIsCheck(true);
            documentsRepository.save(document);

        } catch (DocumentException e) {
            throw new RuntimeException("Document error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred: " + e.getMessage(), e);
        }
    }
}
