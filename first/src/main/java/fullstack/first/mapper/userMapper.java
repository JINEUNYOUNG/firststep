package fullstack.first.mapper;

import fullstack.first.vo.SignForm;
import fullstack.first.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface userMapper {
    User findById(String id) throws Exception;
    void signup(SignForm signForm) throws Exception;
}
