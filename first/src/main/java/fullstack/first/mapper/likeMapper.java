package fullstack.first.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface likeMapper {
    int increaseLike(@Param("user_idx") int user_idx, @Param("board_idx") int board_idx) throws Exception;
    int checkLike(@Param("user_idx") int user_idx, @Param("board_idx") int board_idx) throws Exception;
}
