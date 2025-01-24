package com.bit.board_backend.service;

import com.bit.board_backend.model.BoardDTO;
import lombok.AllArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {
    private SqlSession sqlSession;
    private final String NAMESPACE = "mappers.BoardMapper";
    private final int SIZE = 15;

    // 페이지에 따라 게시글 목록 불러오기
    public List<BoardDTO> selectByPage(int page) {
        HashMap<String, Integer> paramMap = new HashMap<>();
        paramMap.put("startRow", (page - 1) * SIZE);
        paramMap.put("limitSize", SIZE);

        return sqlSession.selectList(NAMESPACE + ".selectByPage", paramMap);
    }

    // 전체 글 불러오기
    public List<BoardDTO> selectAll() {
        return sqlSession.selectList(NAMESPACE + ".selectAll");
    }

    // 전체 글 갯수 불러오기
    public int countAll() {
        return sqlSession.selectOne(NAMESPACE + ".selectMaxPage");
    }

    // 최대 페이지 불러오기
    public int selectMaxPage() {
        int temp = sqlSession.selectOne(NAMESPACE + ".selectMaxPage");

        return temp % SIZE == 0 ? temp / SIZE : (temp / SIZE) + 1;
    }

    // 게시글 하나씩 불러오기
    public BoardDTO selectOne(String id) {

        return sqlSession.selectOne(NAMESPACE + ".selectOne", id);
    }

    // 게시글 입력하기
    public void insert(BoardDTO boardDTO) {
        sqlSession.insert(NAMESPACE + ".insert", boardDTO);
    }
    
    // 게시글 수정하기
    public void update(BoardDTO boardDTO) {
        sqlSession.update(NAMESPACE + ".update", boardDTO);
    }
    
    // 게시글 삭제하기
    public void delete(String id) {
        sqlSession.delete(NAMESPACE + ".delete", id);
    }

}
