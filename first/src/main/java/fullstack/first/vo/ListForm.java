package fullstack.first.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ListForm {
    private int board_idx;
    private String title;
    private String content;
    private String id;
    private int board_type;      //게시판 타입 0~3
    private Timestamp board_reg_date;
    private int board_view;      //조회수
    private int likes;      //조회수
    private boolean notice;      //공지여부
}
