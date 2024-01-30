package fullstack.first.mapper;

import fullstack.first.vo.form.SignForm;
import fullstack.first.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface userMapper {
    UserVO findById(String id) throws Exception;
    void signup(SignForm signForm) throws Exception;
    int checkDuplicate(String loginId) throws Exception;
}
