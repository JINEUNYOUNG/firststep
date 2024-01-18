package fullstack.first.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    private int user_idx;
    private String id;
    private String password;                     //비밀번호(해시)
    private String user_name; 
    private String resident_registration_number; //주민번호(해시)
    private String phone_number;
    private String address;
    private int admin_level;                     //일반0(default) 관리자1
    private Timestamp user_reg_date;
}
