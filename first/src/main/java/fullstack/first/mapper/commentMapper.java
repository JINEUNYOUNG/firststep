package fullstack.first.mapper;

import fullstack.first.vo.CommentVO;
import fullstack.first.vo.form.CommentForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface commentMapper {
    int addComment(@Param("board_idx") int board_idx, @Param("comment_content") String comment_content, @Param("user_idx") int user_idx) throws Exception;
    int addNestedComment(@Param("comment") CommentVO comment) throws Exception;
    List<CommentForm> findCommentByIdx(@Param("idx") int idx) throws Exception;

}
