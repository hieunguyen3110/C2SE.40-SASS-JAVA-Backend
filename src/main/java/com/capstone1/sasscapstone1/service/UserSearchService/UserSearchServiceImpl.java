package com.capstone1.sasscapstone1.service.UserSearchService;

import com.capstone1.sasscapstone1.dto.SearchUserResponseDto.SearchUserResponseDto;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.repository.Account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<List<SearchUserResponseDto>> searchUsersByName(String name, Long loggedInAccountId, int pageNum, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Account> accountPage = accountRepository.findAccountByFirstNameAndLastName(name,name,loggedInAccountId, pageable);

            List<SearchUserResponseDto> searchResults = accountPage.getContent().stream().map(account -> {
                SearchUserResponseDto dto = new SearchUserResponseDto();
                dto.setFirstName(account.getFirstName());
                dto.setLastName(account.getLastName());
                dto.setEmail(account.getEmail());
                dto.setProfilePicture(account.getProfilePicture());
                dto.setMajor(account.getMajor());

                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            throw new RuntimeException("Error searching users: " + e.getMessage(), e);
        }
    }
}
