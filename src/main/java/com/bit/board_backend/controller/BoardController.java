package com.bit.board_backend.controller;

import com.bit.board_backend.model.BoardDTO;
import com.bit.board_backend.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board/")
@AllArgsConstructor
@CrossOrigin("http://localhost:8081")
public class BoardController {
    private final BoardService BOARD_SERVICE;

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
            resultMap.put("boardDTO", boardDTO);
        }

        return resultMap;
    }

    @PostMapping("write")
    public Object write(@RequestBody BoardDTO boardDTO) {
        Map<String, Object> resultMap = new HashMap<>();

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
