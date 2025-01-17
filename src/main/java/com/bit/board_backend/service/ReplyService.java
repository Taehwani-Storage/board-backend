package com.bit.board_backend.service;

import com.bit.board_backend.model.ReplyDTO;
import lombok.AllArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReplyService {
    private SqlSession sqlSession;
    private final String NAMESPACE = "mappers.ReplyMapper";

    public List<ReplyDTO> selectAll(String boardId) {
        return sqlSession.selectList(NAMESPACE + ".selectAll", boardId);
    }

    public void insert(ReplyDTO replyDTO) {
        sqlSession.insert(NAMESPACE + ".insert", replyDTO);
    }

    public void update(ReplyDTO replyDTO) {
        sqlSession.update(NAMESPACE + ".update", replyDTO);
    }

    public void delete(String id) {
        sqlSession.delete(NAMESPACE + ".delete", id);
    }
}
