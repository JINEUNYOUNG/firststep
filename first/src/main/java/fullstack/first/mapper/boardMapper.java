package fullstack.first.mapper;

import fullstack.first.vo.ListForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface boardMapper {
    List<ListForm> getList(@Param("num") int num, @Param("page") int page) throws Exception;
    int getTotalPage(@Param("num") int num) throws Exception;

}
