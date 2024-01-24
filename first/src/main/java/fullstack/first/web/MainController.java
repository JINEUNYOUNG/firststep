package fullstack.first.web;

import fullstack.first.service.BoardService;
import fullstack.first.service.CommentService;
import fullstack.first.service.LoginService;
import fullstack.first.service.SignupService;
import fullstack.first.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    @Autowired
    public LoginService loginService;
    @Autowired
    public SignupService signupService;
    @Autowired
    public BoardService boardService;
    @Autowired
    public CommentService commentService;


    //메인화면. 세션에 로그인정보가 있으면 모델에 넣어준다.
    @GetMapping("/")
    public String index(@SessionAttribute(name = SessionConstants.LOGIN_USER, required = false) User loginUser, Model model) throws Exception {
        if (loginUser == null) {
            return "index";
        }
        model.addAttribute("loginUser", loginUser);
        return "index";
    }

    //로그인화면(get)
    @GetMapping("login")
    public String loginpage(Model model) throws Exception {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    //로그인처리(post) loginForm에 담은 id, pw 받아와서 대조 후 로그인
    @PostMapping("login")
    public String login(@ModelAttribute @Validated LoginForm loginForm, HttpServletRequest request,
                        BindingResult bindingResult, @RequestParam(defaultValue = "/") String redirectURL) throws Exception {
        //타입에러
        if (bindingResult.hasErrors()) {
            return "login";
        }
        //id, pw로 db와 대조 후 에러
        User loginUser = loginService.login(loginForm.getId(), loginForm.getPassword());
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }
        //로그인 성공
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.LOGIN_USER, loginUser);   // 세션에 로그인 회원 정보 보관
        return "redirect:/";
    }

    //로그아웃처리
    @GetMapping("logout")
    public String logout(HttpServletRequest request, Model model) throws Exception {
        //세션과 모델 비움
        HttpSession session = request.getSession();
        session.removeAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("loginUser", null);
        return "redirect:/";
    }

    //회원가입화면
    @GetMapping("signup")
    public String signup() throws Exception {
        return "signup";
    }

    //회원가입 signForm에 담긴 정보로 가입
    @PostMapping("signup")
    public String signupUser(@ModelAttribute("signForm") SignForm signForm,
                             Model model) throws Exception {
        //System.out.println(signForm.toString());
        signupService.signup(signForm);
        System.out.println(signForm.toString());
        return "/login";
    }

    //게시판리스트. 1페이지 분량의 list를 담아 반환
    @GetMapping("boardlist")
    public String boardlist(Model model, @RequestParam(name = "num", required = false) int num,
                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) throws Exception {
        //게시판종류+페이지번호를 받아 리스트반환
        List<ListForm> boardlist = boardService.getList(num, page);
        List<ListForm> noticelist = boardService.getNotice(num);
        if (boardlist != null) {
            model.addAttribute("boardlist", boardlist);
        } else {
            model.addAttribute("boardlist", new ArrayList<Board>());
        }
        if (noticelist != null) {
            model.addAttribute("noticelist", noticelist);
        } else {
            model.addAttribute("noticelist", new ArrayList<Board>());
        }
        //페이지정보 반환
        int totalPage = boardService.getTotalPage(num);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        return "boardlist";
    }

    //상세글화면. 현재 세션(로그인)과 게시물 하나의 정보를 가져와 반환
    @GetMapping("board")
    public String board(HttpSession session, Model model, @RequestParam(name = "idx", required = false) int idx) throws Exception {
        //게시글정보
        ListForm singleBoard = boardService.findBoardByIdx(idx);
        model.addAttribute("board", singleBoard);
        //댓글정보
        List<CommentForm> comments = commentService.findCommentByIdx(idx);
        model.addAttribute("comments", comments);
        //로그인유저정보
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("loginUser", loginUser);
        return "board";
    }

    //글작성화면. 작성자확인을 위해 현재 로그인정보를 넣어준다.
    @GetMapping("write")
    public String write(HttpSession session, Model model) throws Exception {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("loginUser", loginUser);
        return "write";
    }

    @GetMapping("modify")
    public String modify(HttpSession session, Model model, @RequestParam(name = "board_idx", required = false) int board_idx) throws Exception {
        ListForm singleBoard = boardService.findBoardByIdx(board_idx);
        model.addAttribute("board", singleBoard);
        return "modify";
    }

    /**
     * openAPI 로 실시간 날씨 가져와보기
     *
     */

    //openapi시도
    @Autowired
    private OpenApi openApi;

    @GetMapping("api")
    public String openApi(Model model) {
        String[] api = openApi.fetchDataAndPrint();
        model.addAttribute("api", api);
        return "api";
    }

}
