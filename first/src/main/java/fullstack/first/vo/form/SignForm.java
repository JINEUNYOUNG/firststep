package fullstack.first.vo.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignForm {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 영문 대소문자와 숫자로 4~12자리로 작성하세요.")
    private String id;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;    //비밀번호(해시)

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "이름은 특문없이 한글 혹은 영어로 작성하세요.")
    private String user_name;

    @NotBlank(message = "주민등록번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{13}$", message = "특문없이 13자리 숫자로 기입해주세요.")
    private String resident_registration_number; //주민번호(해시)

    @NotBlank(message = "휴대폰번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^[0-9]{11}$", message = "특수문자 제외 11자리 숫자로 기입해주세요.")
    private String phone_number;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
}
