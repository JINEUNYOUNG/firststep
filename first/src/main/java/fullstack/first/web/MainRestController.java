package fullstack.first.web;

import fullstack.first.service.BoardService;
import fullstack.first.service.LoginService;
import fullstack.first.service.SignupService;
import fullstack.first.vo.Board;
import fullstack.first.vo.LoginForm;
import fullstack.first.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class MainRestController {

    @Autowired
    public LoginService loginService;
    @Autowired
    public SignupService signupService;
    @Autowired
    public BoardService boardService;

    //axios를 통한 비동기 로그인 api
    @PostMapping("/login2")
    public ResponseEntity<?> login2(@RequestBody @Validated LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 누락 또는 타입에 맞지 않습니다.");
        }

        User loginUser = loginService.login(loginForm.getId(), loginForm.getPassword());
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 맞지 않습니다.");
        }
        // 로그인 성공 처리
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.LOGIN_USER, loginUser);   // 세션에 로그인 회원 정보 보관
        return ResponseEntity.ok().body("로그인 성공");
    }

    //id 중복확인 api
    @GetMapping("/checkDuplicate")
    public ResponseEntity<String> checkDuplicate(@RequestParam String loginId) throws Exception {
        if (signupService.checkDuplicate(loginId)) {
            return ResponseEntity.ok().body("사용가능합니다.");
        } else {
            return ResponseEntity.badRequest().body("중복된 아이디입니다.");
        }
    }

    //로그인 검증 api
    @GetMapping("/checkAuthority")
    public ResponseEntity<String> checkAuthority(HttpSession session) {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if (loginUser == null) {
            System.out.println("id-null값의 경우");
            return ResponseEntity.badRequest().body("로그인하지 않은 사용자입니다.");
        }
        return ResponseEntity.ok().body("로그인 확인되었습니다.");
    }

    //좋아요+1 api
    @PostMapping("/increaseLike")
    public ResponseEntity<String> increaseLike(HttpSession session, @RequestBody Board board) throws Exception {
        int board_idx = board.getBoard_idx();
        System.out.println(board.toString());
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
         return (boardService.increaseLike(loginUser.getUser_idx(),board_idx))
                 ?  ResponseEntity.ok().body("좋아요성공")
                 :  ResponseEntity.badRequest().body("이미 했습니다.");
        }
}
