package com.capstone1.sasscapstone1.controller.FollowController;

import com.capstone1.sasscapstone1.dto.FollowDto.FollowDto;
import com.capstone1.sasscapstone1.entity.Account;
import com.capstone1.sasscapstone1.entity.Follow;
import com.capstone1.sasscapstone1.exception.FollowException;
import com.capstone1.sasscapstone1.service.FollowService.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow-by-email")
    public ResponseEntity<String> followUserByEmail(@RequestParam String email) throws Exception {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Account account= (Account) authentication.getPrincipal();
            return followService.followUserByEmail(email,account);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You must be login");
        }
    }

    @DeleteMapping("/unfollow-by-email")
    public ResponseEntity<String> unfollowUserByEmail(@RequestParam String email) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Account account= (Account) authentication.getPrincipal();
            return followService.unfollowUserByEmail(email,account);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You must be login");
        }
    }

    @GetMapping("/followers")
    public ResponseEntity<List<FollowDto>> getFollowers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            return followService.getFollowers(account);
        } else {
            throw new FollowException("You are not authorized to perform this action.");
        }
    }

    @GetMapping("/following")
    public ResponseEntity<List<FollowDto>> getFollowing() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            return followService.getFollowing(account);
        } else {
            throw new FollowException("You are not authorized to perform this action.");
        }
    }
}
