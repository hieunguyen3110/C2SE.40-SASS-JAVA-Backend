package com.capstone1.sasscapstone1.service.FollowService;

import com.capstone1.sasscapstone1.dto.FollowDto.FollowDto;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.entity.Follow;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface FollowService {
    ResponseEntity<String> followUserByEmail(String email, Account account) throws Exception;

    ResponseEntity<String> unfollowUserByEmail(String email, Account account);

    ResponseEntity<List<FollowDto>> getFollowers(Account currentUser);

    ResponseEntity<List<FollowDto>> getFollowing(Account currentUser);
}
