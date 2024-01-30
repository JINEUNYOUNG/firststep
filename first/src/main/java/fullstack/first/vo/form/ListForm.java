package fullstack.first.vo.form;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
public class ListForm {
    private int board_idx;
    private String title;
    private String content;
    private String id;
    private int board_type;               //게시판 타입 0~3
    private Timestamp board_reg_date;
    private int board_view;               //조회수
    private int likes;                    //좋아요수
    private boolean notice;               //공지여부
    private int download_lev;             //다운로드권한(3:전체, 2:로그인유저, 1:본인만, 0:관리자만)
    private String file_extension;
    private int file_count;
    private int comment_count;
}
