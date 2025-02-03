package com.bit.board_backend.controller;

import com.bit.board_backend.model.BoardDTO;
import com.bit.board_backend.model.UserDTO;
import com.bit.board_backend.service.BoardService;
import com.bit.board_backend.service.UserService;
import com.bit.board_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board/")
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class BoardController {
    private final BoardService BOARD_SERVICE;
    private final String LIST_FORMATTER = "yy-MM-dd HH:mm:ss";
    private final String INDIV_FORMATTER = "yyyy년 MM월 dd일 HH시 mm분 ss초";
    private final JwtUtil JWT_UTIL;
    private final UserService userService;

    @GetMapping("showAll")
    public Object showAll() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", BOARD_SERVICE.selectAll());
        resultMap.put("total", BOARD_SERVICE.countAll());

        return resultMap;
    }
    @GetMapping("showAll/{page}")
    public Object showAll(@PathVariable String page) {
        Map<String, Object> resultMap = new HashMap<>();

        int pageNo;
        try {
            pageNo = Integer.parseInt(page);
        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());

            return resultMap;
        }

        List<BoardDTO> list = BOARD_SERVICE.selectByPage(pageNo);
        if (list.isEmpty()) {
            resultMap.put("result", "fail");
            resultMap.put("message", "Invalid page number");
        } else {
            resultMap.put("result", "success");
            SimpleDateFormat formatter = new SimpleDateFormat(LIST_FORMATTER);
            for(BoardDTO b : list) {
                b.setFormattedEntryDate(formatter.format(b.getEntryDate()));
                b.setFormattedModifyDate(formatter.format(b.getModifyDate()));
            }
            resultMap.put("list", list);

            int maxPage = BOARD_SERVICE.selectMaxPage();
            int startPage = pageNo - 2;
            int endPage = pageNo + 2;

            if (maxPage <= 5) {
                startPage = 1;
                endPage = maxPage;
            } else if (pageNo <= 3) {
                startPage = 1;
                endPage = 5;
            } else if (pageNo >= maxPage - 2) {
                startPage = maxPage - 4;
                endPage = maxPage;
            }

            resultMap.put("maxPage", maxPage);
            resultMap.put("startPage", startPage);
            resultMap.put("endPage", endPage);
            resultMap.put("currentPage", pageNo);
        }

        return resultMap;
    }

    @GetMapping("showOne/{id}")
    public Object showOne(@PathVariable String id) {
        Map<String, Object> resultMap = new HashMap<>();

        BoardDTO boardDTO = BOARD_SERVICE.selectOne(id);

        if (!id.matches("^\\d+$") || boardDTO == null) {
            resultMap.put("result", "fail");
            resultMap.put("message", "Invalid board content");
        } else {
            resultMap.put("result", "success");
            SimpleDateFormat sdf = new SimpleDateFormat(INDIV_FORMATTER);
            boardDTO.setFormattedEntryDate(sdf.format(boardDTO.getEntryDate()));
            boardDTO.setFormattedModifyDate(sdf.format(boardDTO.getModifyDate()));
            resultMap.put("boardDTO", boardDTO);
        }

        return resultMap;
    }

    @PostMapping("write")
    public Object write(@RequestBody BoardDTO boardDTO, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String token = authHeader.substring(7);
        String username = JWT_UTIL.validateToken(token);
        UserDTO userDTO = userService.loadByUsername(username);
        boardDTO.setWriterId(userDTO.getId());

        try {
            BOARD_SERVICE.insert(boardDTO);
            resultMap.put("result", "success");
            resultMap.put("boardDTO", boardDTO);
        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());
        }

        return resultMap;
    }

    @PostMapping("update")
    public Object update(@RequestBody BoardDTO boardDTO) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            BOARD_SERVICE.update(boardDTO);
            resultMap.put("result", "success");
            resultMap.put("boardDTO", boardDTO);

        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());
        }

        return resultMap;
    }

    @GetMapping("delete/{id}")
    public Object delete(@PathVariable String id) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            BOARD_SERVICE.delete(id);
            resultMap.put("result", "success");
        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());
        }

        return resultMap;
    }
}
