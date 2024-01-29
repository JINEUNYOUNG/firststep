package fullstack.first.web;

import fullstack.first.service.*;
import fullstack.first.vo.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Autowired
    public FileService fileService;


    //메인화면. 세션에 로그인정보가 있으면 모델에 넣어준다.
    @GetMapping("/")
    public String index(@SessionAttribute(name = SessionConstants.LOGIN_USER, required = false) User loginUser,
                        Model model) {
        if (loginUser == null) {
            return "index";
        }
        model.addAttribute("loginUser", loginUser);
        return "index";
    }

    //로그인화면(get)
    @GetMapping("login")
    public String loginpage(@SessionAttribute(name = "message", required = false) String message, Model model) throws Exception {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("message", message);
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
        signupService.signup(signForm);
        return "redirect:/login";
    }

    //게시판리스트. 1페이지 분량의 list를 가져온다
    @GetMapping("boardlist")
    public String boardlist(Model model, @RequestParam(name = "num", required = false) int num,
                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) throws Exception {
        //게시판종류+페이지번호를 받아 리스트반환 + 공지사항 리스트반환
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
        //첨부파일정보
        model.addAttribute("files", fileService.getFilesByIdx(idx));
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
    public String write(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if (loginUser == null) {
            throw new NullPointerException("로그인해야합니다.");
        }
        model.addAttribute("loginUser", loginUser);
        return "write";
    }


    //글작성
    @PostMapping("write")
    public String write(HttpSession session, Model model, @ModelAttribute WriteForm writeForm) throws Exception {
        int board_idx = boardService.writeBoard(writeForm);
        int result = 0;
        if (!writeForm.getFile().get(0).getOriginalFilename().trim().isEmpty()) {
            result = fileService.addFile(board_idx, writeForm.getFile());
        }
        if (result == 1) {
            return "redirect:/boardlist?num=" + writeForm.getBoard_type();
        } else {
            model.addAttribute("message", "글쓰기 실패하였습니다.");
            return "redirect:/boardlist?num=" + writeForm.getBoard_type();
        }
    }

    //수정페이지
    @GetMapping("modify")
    public String modify(HttpSession session, Model model, @RequestParam(name = "board_idx", required = false) int board_idx) throws Exception {
        ListForm singleBoard = boardService.findBoardByIdx(board_idx);
        model.addAttribute("board", singleBoard);
        return "modify";
    }

    //글 수정기능
    @GetMapping("modifyBoard")
    public String modifyBoard(HttpSession session, Model model, @ModelAttribute WriteForm writeForm) throws Exception {
        System.out.println("여기");
        boardService.modifyBoard(writeForm);
        int result = 0;
        if (!writeForm.getFile().isEmpty()) {
            result = fileService.addFile(writeForm.getBoard_idx(), writeForm.getFile());
        }
        if (result == 1) {
            return "redirect:/boardlist?num=" + writeForm.getBoard_type();
        } else {
            model.addAttribute("message", "수정 실패하였습니다.");
            return "redirect:/boardlist?num=" + writeForm.getBoard_type();
        }
    }

    //엑셀데이터를 받아와 읽어 write화면에 뿌려준다
    @PostMapping("excel/read")
    public String readExcel(@RequestParam("excelFile") MultipartFile file, Model model, HttpSession session) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //엑셀이 아닌 경우
        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }
        Workbook workbook = null;
        //엑셀 버전에 따라 처리
        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }
        //1행의 데이터를 가져와서 WriteForm 객체에 주입후 반환
        Sheet worksheet = workbook.getSheetAt(0);
        Row row = worksheet.getRow(1);
        WriteForm data = new WriteForm();
        data.setTitle(row.getCell(0).getStringCellValue());
        data.setContent(row.getCell(1).getStringCellValue());
        data.setBoard_type((int)row.getCell(2).getNumericCellValue());
        model.addAttribute("excel", data);
        //writer 처리
        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        if (loginUser == null) {
            throw new NullPointerException("로그인해야합니다.");
        }
        model.addAttribute("loginUser", loginUser);

        return "write";
    }

    /**
     * openAPI 로 실시간 날씨 가져와보기
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
