package fullstack.first.mapper;

import fullstack.first.vo.FileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface fileMapper {
    int addFile(@Param("board_idx") int board_idx, @Param("files") FileVO files) throws Exception;
    List<FileVO> getFilesByIdx(@Param("board_idx") int board_idx) throws Exception;
    FileVO getFileByIdx(@Param("file_idx") int file_idx) throws Exception;

}
