package fullstack.first.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SignForm {
    private String id;
    private String password;                     //비밀번호(해시)
    private String user_name; 
    private Integer resident_registration_number; //주민번호(해시)
    private Integer phone_number;
    private String address;
}
