package fullstack.first.service;

import fullstack.first.mapper.userMapper;
import fullstack.first.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;


@RequiredArgsConstructor
@Service
public class LoginService {

    @Autowired
    public userMapper mapper;

    //id를 보내 유저를 찾아 유저객체를 받아준다. 패스워드는 암호화(SHA256)하여 동일하면 유저객체반환 else null
    public UserVO login(String id, String password) throws Exception {
        if (mapper.findById(id) == null) {
            return null;
        }
        if (hashPassword(password).equals(mapper.findById(id).getPassword())) {
            return mapper.findById(id);
        } return null;
    }

    //비밀번호 해시(SHA256)거는 메소드
    private static String hashPassword(String password) throws Exception {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
    }
}
