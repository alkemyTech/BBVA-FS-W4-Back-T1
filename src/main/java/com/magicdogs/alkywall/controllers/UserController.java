package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.servicies.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<UserPageDTO> userList(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10")int size){
        Page<UserDTO> usersPage = userService.getUsers(page, size);
        String next = "", prev = "";
        if(usersPage.hasNext()){
            next = "/users?page="+(page+1);
        }
        if(usersPage.hasPrevious()){
            prev = "/users?page="+(page-1);
        }
        return ResponseEntity.ok(new UserPageDTO(usersPage.getContent(), next, prev));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> userDelete(@PathVariable("id") Long id){
        userService.softDeleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
