package fullstack.first.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommentForm {
    private int comment_idx;
    private String id;
    private int board_idx;
    private String comment_content;
    private Timestamp comment_reg_date;
    private int comment_class; //부모 0 자식 1 기본 0
    private int comment_group; //부모의 index를 가짐
}
