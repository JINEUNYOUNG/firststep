package fullstack.first.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@Data
public class WriteForm {
    private String title;
    private String content;
    private int user_idx;
    private int board_type;      //게시판 타입 0~3
    private int download_lev;    //다운로드권한(3:전체, 2:로그인유저, 1:본인만, 0:관리자만) defalut 0
    private List<MultipartFile> file;    //파일
}
