package fullstack.first.web;

import fullstack.first.service.BoardService;
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


    //메인. 세션에 로그인정보가 있으면 모델에 넣어준다.
    @GetMapping("/")
    public String index(@SessionAttribute(name = SessionConstants.LOGIN_USER, required = false) User loginUser, Model model) throws Exception{

        if (loginUser == null){
            return "index";
        }
        System.out.println(loginUser.toString());
        model.addAttribute("loginUser", loginUser);
        return "index";
    }
    @GetMapping("login")
    public String loginpage(Model model) throws Exception{
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    //id, pw 받아와서 대조 후 로그인처리
    @PostMapping("login")
    public String login(@ModelAttribute @Validated LoginForm loginForm, HttpServletRequest request,
                        BindingResult bindingResult, @RequestParam(defaultValue = "/") String redirectURL) throws Exception{
        if (bindingResult.hasErrors()) {
            return "login";
        }
        User loginUser = loginService.login(loginForm.getId(),loginForm.getPassword());
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login";
        }
        // 로그인 성공 처리
        HttpSession session = request.getSession();
        session.setAttribute(SessionConstants.LOGIN_USER, loginUser);   // 세션에 로그인 회원 정보 보관
        return "redirect:/";
    }

    //로그아웃 처리 
    @GetMapping("logout")
    public String logout(HttpServletRequest request, Model model) throws Exception{
        //세션과 모델 비움
        HttpSession session = request.getSession();
        session.removeAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("loginUser",null);

        return "redirect:/";
    }

    @GetMapping("signup")
    public String signup() throws Exception{
        return "signup";
    }

    @PostMapping("signup")
    public String signupUser(@ModelAttribute("signForm") SignForm signForm,
                                         Model model) throws Exception{
        //System.out.println(signForm.toString());
        signupService.signup(signForm);
        return "redirect:/";
    }

    //1페이지 분량의 list를 담아 반환
    @GetMapping("boardlist")
    public String boardlist(Model model, @RequestParam(name = "num", required = false) int num,
                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) throws Exception{
        //게시판종류+페이지번호를 받아 리스트반환
        List<ListForm> boardlist = boardService.getList(num,page);
        List<ListForm> noticelist = boardService.getNotice(num);
        if (boardlist != null) {
            model.addAttribute("boardlist", boardlist);
        } else {
            model.addAttribute("boardlist",new ArrayList<Board>());
        }
        if (noticelist != null) {
            model.addAttribute("noticelist", noticelist);
        } else {
            model.addAttribute("noticelist",new ArrayList<Board>());
        }
        //페이지정보 반환
        int totalPage = boardService.getTotalPage(num);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        return "boardlist";
    }

    @GetMapping("board")
    public String board(HttpSession session, Model model, @RequestParam(name = "idx", required = false) int idx) throws Exception{
        ListForm singleBoard = boardService.findBoardByIdx(idx);
        model.addAttribute("board", singleBoard);

        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("loginUser", loginUser);


        return "board";
    }

    @GetMapping("write")
    public String write(HttpSession session, Model model) throws Exception {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        model.addAttribute("loginUser",loginUser);
        return "write";
    }



    @Autowired
    private OpenApi openApi;

    @GetMapping("api")
    public String openApi(Model model){
        String[] api = openApi.fetchDataAndPrint();
        model.addAttribute("api",api);
        return "api";
    }

}
