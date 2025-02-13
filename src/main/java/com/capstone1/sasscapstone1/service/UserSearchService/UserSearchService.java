package com.capstone1.sasscapstone1.service.UserSearchService;

import com.capstone1.sasscapstone1.dto.SearchUserResponseDto.SearchUserResponseDto;
import com.capstone1.sasscapstone1.dto.UserProfileResponseDTO.UserProfileResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserSearchService {
    ResponseEntity<List<SearchUserResponseDto>> searchUsersByName(String name, Long accountId,int pageNum,int pageSize);
}
