package com.bit.board_backend.controller;

import com.bit.board_backend.model.UserDTO;
import com.bit.board_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/")
@AllArgsConstructor
@CrossOrigin("http://localhost:8081")
public class UserController {
    private UserService USER_SERVICE;

    // 로그인 요청에 대한 결과 처리
    @PostMapping("auth")
    public Object auth(@RequestBody UserDTO userDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        UserDTO result = USER_SERVICE.auth(userDTO);
        if (result != null) {
            resultMap.put("result", "success");
            resultMap.put("logIn", result);
        } else {
            resultMap.put("result", "fail");
            resultMap.put("message", "Check your ID or password");
        }
        return resultMap;
    }

    // 회원 가입 요청에 대한 결과 처리
    @PostMapping("register")
    public Object register(@RequestBody UserDTO userDTO) {
        System.out.println(userDTO);

        Map<String, Object> resultMap = new HashMap<>();

        if (!USER_SERVICE.validateUsername(userDTO)) {
            resultMap.put("result", "fail");
            resultMap.put("message", "Duplicated username");
        } else if (!USER_SERVICE.validateNickname(userDTO)) {
            resultMap.put("result", "fail");
            resultMap.put("message", "Duplicated nickname");
        } else {
            USER_SERVICE.register(userDTO);
            resultMap.put("result", "success!");
        }

        return resultMap;
    }

}
