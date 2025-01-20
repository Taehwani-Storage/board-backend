package com.bit.board_backend.controller;

import com.bit.board_backend.model.BoardDTO;
import com.bit.board_backend.model.ReplyDTO;
import com.bit.board_backend.service.ReplyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/reply/")
@AllArgsConstructor
@CrossOrigin("http://localhost:8081")
public class ReplyController {
    private final ReplyService REPLY_SERVICE;

    @GetMapping("showAll/{boardId}")
    public Object showAll(@PathVariable String boardId) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            List<ReplyDTO> list = REPLY_SERVICE.selectAll(boardId);

            resultMap.put("result", "success");
            resultMap.put("list", list);

        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());
        }

        return resultMap;
    }

    @PostMapping("write")
    public Object write(@RequestBody ReplyDTO replyDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            REPLY_SERVICE.insert(replyDTO);
            resultMap.put("result", "success");
            resultMap.put("boardDTO", replyDTO);
        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());
        }

        return resultMap;
    }

    @PostMapping("update")
    public Object update(@RequestBody ReplyDTO replyDTO) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            REPLY_SERVICE.update(replyDTO);
            resultMap.put("result", "success");
            resultMap.put("boardDTO", replyDTO);

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
            REPLY_SERVICE.delete(id);
            resultMap.put("result", "success");
        } catch (Exception e) {
            resultMap.put("result", "fail");
            resultMap.put("message", e.getMessage());
        }

        return resultMap;
    }

}
