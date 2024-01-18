package fullstack.first.web;

import fullstack.first.service.LoginService;
import fullstack.first.vo.LoginForm;
import fullstack.first.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class MainRestController {

    @Autowired
    public LoginService loginService;

    @PostMapping("/login2")
    public ResponseEntity<?> login2(@RequestBody @Validated LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        if (bindingResult.hasErrors()) {
            // 유효성 검증 에러가 있을 경우 에러 응답을 클라이언트로 전송
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 누락 또는 타입에 맞지 않습니다.");
        }

        User loginUser = loginService.login(loginForm.getId(),loginForm.getPassword());
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 맞지 않습니다.");
        }
        // 로그인 성공 처리
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.LOGIN_USER, loginUser);   // 세션에 로그인 회원 정보 보관

        return ResponseEntity.ok().body("로그인 성공");
    }



}
