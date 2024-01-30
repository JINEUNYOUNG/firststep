package fullstack.first.service;


import fullstack.first.mapper.userMapper;
import fullstack.first.vo.form.SignForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

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
        return count == 0;         // 0==true==중복이 아님
    }
    //회원가입 시 유효성체크
    public Map<String, String> validateHandling(Errors errors) throws Exception{
        Map<String, String> validatorResult = new HashMap<>();
        for (FieldError error : errors.getFieldErrors()){
            String validKeyName = String.format("valid_%s", error.getField()); //key 구성이 Key : valid_{dto 필드명} 이렇게
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
}
