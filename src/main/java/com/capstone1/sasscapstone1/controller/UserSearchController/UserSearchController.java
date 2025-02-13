package com.capstone1.sasscapstone1.controller.UserSearchController;

import com.capstone1.sasscapstone1.dto.SearchUserResponseDto.SearchUserResponseDto;
import com.capstone1.sasscapstone1.dto.UserProfileResponseDTO.UserProfileResponse;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.repository.Account.AccountRepository;
import com.capstone1.sasscapstone1.service.UserSearchService.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-search")
public class UserSearchController {

    private final UserSearchService userSearchService;

    @Autowired
    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    @GetMapping("/search-by-name")
    public ResponseEntity<List<SearchUserResponseDto>> searchUsersByName(@RequestParam String name,
                                                                         @RequestParam(defaultValue = "0") int pageNum,
                                                                         @RequestParam(defaultValue = "5") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account loggedInAccount = (Account) authentication.getPrincipal();

            return userSearchService.searchUsersByName(name, loggedInAccount.getAccountId(),pageNum,pageSize);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }    }
}
