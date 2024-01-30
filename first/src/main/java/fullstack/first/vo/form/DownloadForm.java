package fullstack.first.vo.form;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DownloadForm {
    private int file_idx;
    private int board_idx;
    private int user_idx;
    private int download_lev;
    private String id;
}
