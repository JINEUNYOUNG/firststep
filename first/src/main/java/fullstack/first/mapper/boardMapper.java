package fullstack.first.mapper;

import fullstack.first.vo.ListForm;
import fullstack.first.vo.WriteForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface boardMapper {
    List<ListForm> getList(@Param("num") int num, @Param("page") int page) throws Exception;
    List<ListForm> getNotice(@Param("num") int num) throws Exception;
    int getTotalPage(@Param("num") int num) throws Exception;
    ListForm findBoardByIdx(@Param("idx") int idx) throws Exception;
    void increaseView(@Param("idx") int idx) throws Exception;
    int getBoardWriter(@Param("board_idx") int board_idx) throws Exception;
    void deleteBoard(@Param("board_idx") int board_idx) throws Exception;
    void setNotice(@Param("board_idx") int board_idx) throws Exception;
    void cancelNotice(@Param("board_idx") int board_idx) throws Exception;
    int writeBoard(@Param("writeForm")WriteForm writeForm) throws Exception;
    int getBoardIdx() throws Exception;
}
