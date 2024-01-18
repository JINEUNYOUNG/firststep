package fullstack.first.vo;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Comment {
    private int comment_idx;
    private int user_idx;
    private int board_idx;
    private String comment_content;
    private Timestamp comment_reg_date;
}
