package fullstack.first.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BoardVO {
    private int board_idx;
    private String title;
    private String content;
    private int user_idx;
    private int board_type;      //게시판 타입 1~3
    private Timestamp board_reg_date;
    private int board_view;      //조회수
    private boolean notice;      //공지여부
    private int download_lev;    //다운로드권한(3:전체, 2:로그인유저, 1:본인만, 0:관리자만) defalut 0
}
