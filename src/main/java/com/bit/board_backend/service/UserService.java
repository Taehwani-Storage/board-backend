package com.bit.board_backend.service;

import com.bit.board_backend.model.UserDTO;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final String NAMESPACE = "mappers.UserMapper";
    private SqlSession sqlSession;

    // 데이터베이스와 통신하여 username, pw가 같은 회원 찾기
    public UserDTO auth(UserDTO userDTO) {
        return sqlSession.selectOne(NAMESPACE + ".auth", userDTO);
    }

    // 유효한 username인지 체크
    public boolean validateUsername(UserDTO userDTO) {
        return sqlSession.selectOne(NAMESPACE + ".validateUsername", userDTO) == null;
    }
    // 유효한 nickname인지 체크
    public boolean validateNickname(UserDTO userDTO) {
        return sqlSession.selectOne(NAMESPACE + ".validateNickname", userDTO) == null;
    }

    // 회원가입 하기
    public void register(UserDTO userDTO) {
        sqlSession.selectOne(NAMESPACE + ".register", userDTO);
    }

    public UserDTO loadByUsername(String username) {
        return sqlSession.selectOne(NAMESPACE + ".loadByUsername", username);
    }

}
