package fullstack.first.web;

import fullstack.first.service.LoginService;
import fullstack.first.service.SignupService;
import fullstack.first.vo.LoginForm;
import fullstack.first.vo.SignForm;
import fullstack.first.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class MainController {

    @Autowired
    public LoginService loginService;
    @Autowired
    public SignupService signupService;


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


        System.out.println(signForm.toString());


        signupService.signup(signForm);

        return "redirect:/";
    }

}
