package com.capstone1.sasscapstone1.service.FollowService;

import com.capstone1.sasscapstone1.dto.FollowDto.FollowDto;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.entity.Follow;
import com.capstone1.sasscapstone1.exception.FollowException;
import com.capstone1.sasscapstone1.repository.Account.AccountRepository;
import com.capstone1.sasscapstone1.repository.Follow.FollowRepository;
import com.capstone1.sasscapstone1.service.KafkaService.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaService kafkaService;

    @Override
    public ResponseEntity<String> followUserByEmail(String email, Account account) {
        try {
            // Kiểm tra người dùng cần follow có tồn tại không
            Account accountToFollow = accountRepository.findAccountByEmail(email)
                    .orElseThrow(() -> new FollowException("User with email " + email + " not found"));

            // Kiểm tra người dùng có thể không tự follow chính mình
            if (Long.valueOf(account.getAccountId()).equals(accountToFollow.getAccountId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot follow yourself.");
            }

            // Tạo bản ghi follow
            Follow follow = new Follow();
            follow.setFollower(account);
            follow.setFollowing(accountToFollow);

            followRepository.save(follow);

            kafkaService.sendNotificationFromUserFollowing(accountToFollow, account.getLastName());

            return ResponseEntity.ok("You are now following " + accountToFollow.getEmail());
        } catch (FollowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error following user: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> unfollowUserByEmail(String email, Account account) {
        try {
            // Kiểm tra người dùng cần unfollow có tồn tại không
            Account accountToUnfollow = accountRepository.findAccountByEmail(email)
                    .orElseThrow(() -> new FollowException("User with email " + email + " not found"));

            // Kiểm tra người dùng có thể không tự unfollow chính mình
            if (Long.valueOf(account.getAccountId()).equals(accountToUnfollow.getAccountId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot unfollow yourself.");
            }

            // Xóa bản ghi follow
            Follow follow = followRepository.findByFollowerAndFollowing(account, accountToUnfollow)
                    .orElseThrow(() -> new FollowException("Follow relationship not found"));

            followRepository.delete(follow);

            return ResponseEntity.ok("You have unfollowed " + accountToUnfollow.getEmail());
        } catch (FollowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error unfollowing user: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<FollowDto>> getFollowers(Account account) {
        try {
            // Lấy danh sách các followers của người dùng
            List<Follow> followers = followRepository.findByFollowing(account);
            if (followers.isEmpty()) {
                throw new FollowException("No followers found for this user.");
            }

            // Ánh xạ danh sách Follow sang FollowDto
            List<FollowDto> followerDtos = followers.stream().map(follow -> {
                Account followerAccount = follow.getFollower();
                FollowDto dto = new FollowDto();
                dto.setFollowId(follow.getFollowId());
                dto.setFirstName(followerAccount.getFirstName());
                dto.setLastName(followerAccount.getLastName());
                dto.setProfilePicture(followerAccount.getProfilePicture());
                return dto;
            }).toList();

            return ResponseEntity.ok(followerDtos);
        } catch (FollowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<List<FollowDto>> getFollowing(Account account) {
        try {
            List<Follow> followings = followRepository.findByFollower(account);
            if (followings.isEmpty()) {
                throw new FollowException("You are not following anyone.");
            }

            // Map Follow entity to FollowDto
            List<FollowDto> followingDtos = followings.stream().map(follow -> {
                Account followingAccount = follow.getFollowing();
                FollowDto dto = new FollowDto();
                dto.setFollowId(follow.getFollowId());
                dto.setFirstName(followingAccount.getFirstName());
                dto.setLastName(followingAccount.getLastName());
                dto.setProfilePicture(followingAccount.getProfilePicture());
                return dto;
            }).toList();

            return ResponseEntity.ok(followingDtos);
        } catch (FollowException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
