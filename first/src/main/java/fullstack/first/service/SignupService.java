package fullstack.first.service;


import fullstack.first.mapper.userMapper;
import fullstack.first.vo.SignForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class SignupService {

    @Autowired
    public userMapper mapper;

    public void signup(SignForm signForm) throws Exception {
        mapper.signup(signForm);
    }
    public boolean checkDuplicate(String loginId) throws Exception{
        int count = mapper.checkDuplicate(loginId);
        // 0==true==중복이 아님
        // false!=0==중복
        return count == 0;

    }
}
