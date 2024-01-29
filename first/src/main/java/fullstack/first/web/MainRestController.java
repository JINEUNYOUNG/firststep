package fullstack.first.web;

import fullstack.first.service.*;
import fullstack.first.vo.*;
import fullstack.first.vo.Comment;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    @Autowired
    public FileService fileService;

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
        return (commentService.addComment(comment))
                ? ResponseEntity.ok().body("댓글작성완료")
                : ResponseEntity.badRequest().body("실패");
    }

    //다운로드 선택 시, 권한 비교 후 url반환
    @PostMapping("download")
    public ResponseEntity<Resource> download(HttpSession session, @RequestBody DownloadForm downloadForm) throws Exception {

        User loginUser = (User) session.getAttribute(SessionConstants.LOGIN_USER);
        //널값 처리 먼저
        if (loginUser == null) {
            if (downloadForm.getDownload_lev() == 3) {
                return performDownload(downloadForm.getFile_idx()); //전체면 다운로드 되게
            } else {
                return new ResponseEntity<>(new ByteArrayResource("로그인하세요.".getBytes()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        //권한에 따른 다운로드 처리
        switch (downloadForm.getDownload_lev()) {
            case 0: //관리자만
                if (loginUser.getId().equals("admin")) {
                    return performDownload(downloadForm.getFile_idx());
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ByteArrayResource("관리자만 다운로드 가능합니다.".getBytes()));
                }
            case 1: //몬인만
                if (loginUser.getId().equals(downloadForm.getId()) || loginUser.getUser_idx() == 1) {
                    return performDownload(downloadForm.getFile_idx());
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ByteArrayResource("관리자 또는 작성자만 다운로드 가능합니다.".getBytes()));
                }
            case 2:
            case 3: //전체
                return performDownload(downloadForm.getFile_idx());
        }
        return ResponseEntity.badRequest().body(new ByteArrayResource("실패".getBytes()));
    }

    //실제 다운로드 데이터 받아오는 메서드
    public ResponseEntity<Resource> performDownload(int file_idx) {
        try {
            FileVO file = fileService.getFileByIdx(file_idx);

            // 파일 데이터를 가져오는 코드로 대체
            byte[] fileData = fileService.getFileData(file);  // 파일 데이터 가져오는 메서드 사용

            String encodeUploadFileName = UriUtils.encode(file.getOriginal_name(), StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", encodeUploadFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // ResponseEntity로 파일과 헤더 정보 반환
            return new ResponseEntity<>(new ByteArrayResource(fileData), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(new ByteArrayResource(("다운로드 중 오류가 발생했습니다. 오류 메시지: " + e.getMessage()).getBytes()));
        }
    }

    //엑셀로 내보내기
    @GetMapping("/exportExcel")
    public ResponseEntity<String> exportExcel(@RequestParam int board_idx, HttpServletResponse response) throws Exception {
        try {
            ListForm board = boardService.findBoardByIdx(board_idx);
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(board_idx + "번글");
            int rowNo = 0;
            Row headerRow = sheet.createRow(rowNo++);
            headerRow.createCell(0).setCellValue("게시판번호");
            headerRow.createCell(1).setCellValue("게시글번호");
            headerRow.createCell(2).setCellValue("제목");
            headerRow.createCell(3).setCellValue("내용");
            headerRow.createCell(4).setCellValue("작성자");
            headerRow.createCell(5).setCellValue("작성일자");
            headerRow.createCell(6).setCellValue("파일확장자");
            headerRow.createCell(7).setCellValue("파일수");
            headerRow.createCell(8).setCellValue("다운로드권한");

            Row row = sheet.createRow(rowNo++);

            row.createCell(0).setCellValue(board.getBoard_type());
            row.createCell(1).setCellValue(board.getBoard_idx());
            row.createCell(2).setCellValue(board.getTitle());
            row.createCell(3).setCellValue(board.getContent());
            row.createCell(4).setCellValue(board.getId());
            row.createCell(5).setCellValue(board.getBoard_reg_date()+"");
            if (board.getFile_extension() == null) {
                row.createCell(6).setCellValue("-");
            } else {
                row.createCell(6).setCellValue(board.getFile_extension());
            }
            row.createCell(7).setCellValue(board.getFile_count());
            row.createCell(8).setCellValue(board.getDownload_lev());

            String savePath = System.getProperty("user.dir") + "\\upload\\" + "board_idx_" + board.getBoard_idx() + ".xls";
            File file = new File(savePath);
            workbook.write(new FileOutputStream(file));
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 생성 중 오류가 발생했습니다.");
        }
        return ResponseEntity.ok().body("다운로드되었습니다.");

    }
}
