package fullstack.first.web;

import fullstack.first.service.*;
import fullstack.first.vo.Board;
import fullstack.first.vo.Comment;
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
    @Autowired
    public LikeService likeService;
    @Autowired
    public CommentService commentService;

    //axios를 통한 비동기 로그인 api
    @PostMapping("/login2")
    public ResponseEntity<?> login2(@RequestBody @Validated LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        //타입불일치
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 누락 또는 타입에 맞지 않습니다.");
        }
        //id, pw 대조 후
        User loginUser = loginService.login(loginForm.getId(), loginForm.getPassword());
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 맞지 않습니다.");
        }
        //로그인 성공 후 세션에 로그인 정보 보관
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.LOGIN_USER, loginUser);
        return ResponseEntity.ok().body("로그인 성공");
    }

    //id 중복확인 api
    @GetMapping("/checkDuplicate")
    public ResponseEntity<String> checkDuplicate(@RequestParam String loginId) throws Exception {
        return (signupService.checkDuplicate(loginId))
                ? ResponseEntity.ok().body("사용가능합니다.")
                : ResponseEntity.badRequest().body("중복된 아이디입니다.");
    }

    //로그인 검증 api
    @GetMapping("/checkAuthority")
    public ResponseEntity<String> checkAuthority(HttpSession session) {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        return (loginUser == null)
                ? ResponseEntity.badRequest().body("로그인하지 않은 사용자입니다.")
                : ResponseEntity.ok().body("로그인 확인되었습니다.");
    }

    //좋아요+1 api
    @PostMapping("/increaseLike")
    public ResponseEntity<String> increaseLike(HttpSession session, @RequestBody Board board) throws Exception {
        int board_idx = board.getBoard_idx();
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        return (likeService.increaseLike(loginUser.getUser_idx(), board_idx))
                ? ResponseEntity.ok().body("좋아요성공")
                : ResponseEntity.badRequest().body("이미 했습니다.");
    }

    //글삭제 api
    @DeleteMapping("/deleteBoard")
    public ResponseEntity<String> deleteBoard(HttpSession session, @RequestParam int board_idx) throws Exception {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        return (boardService.deleteBoard(board_idx, loginUser.getUser_idx()) == 0)
                ? ResponseEntity.badRequest().body("권한이 없습니다.")
                : ResponseEntity.ok().body("삭제완료");
    }

    @GetMapping("/setNotice")
    public ResponseEntity<String> setNotice(HttpSession session, @RequestParam int board_idx) throws Exception {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if (loginUser.getUser_idx() == 1) {
            boardService.setNotice(board_idx);
            return ResponseEntity.ok().body("공지지정완료");
        } else {
            return ResponseEntity.badRequest().body("권한이 없습니다.");
        }
    }

    @GetMapping("/cancelNotice")
    public ResponseEntity<String> cancelNotice(HttpSession session, @RequestParam int board_idx) throws Exception {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if (loginUser.getUser_idx() == 1) {
            boardService.cancelNotice(board_idx);
            return ResponseEntity.ok().body("공지취소완료");
        } else {
            return ResponseEntity.badRequest().body("권한이 없습니다.");
        }
    }

    @PostMapping("/addComment")
    public ResponseEntity<String> addComment(HttpSession session, @RequestBody Comment comment) throws Exception {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        comment.setUser_idx(loginUser.getUser_idx());
        System.out.println(comment.toString());
        return (commentService.addComment(comment))
                ? ResponseEntity.ok().body("댓글작성완료")
                : ResponseEntity.badRequest().body("실패");
    }
}
