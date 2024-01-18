package fullstack.first.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class File {
    private int file_idx;
    private int board_idx;
    private String file_name;       //파일 저장명
    private String original_name;   //기존 파일명
    private int file_size;          //사이즈
    private String file_extension;  //확장자
    private Timestamp file_reg_date;
}
