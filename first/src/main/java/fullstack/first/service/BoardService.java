package fullstack.first.service;

import fullstack.first.mapper.boardMapper;
import fullstack.first.vo.Board;
import fullstack.first.vo.ListForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;


@RequiredArgsConstructor
@Service
public class BoardService {

    @Autowired
    public boardMapper mapper;

    public List<ListForm> getList(int num, int page) throws Exception {
        //offset으로 시작번호 지정해주기 위해서 page 조정
        page = (page-1) * 10;
        return mapper.getList(num, page);
    }
    public List<ListForm> getNotice(int num) throws Exception {
        return mapper.getNotice(num);
    }

    public int getTotalPage(int num) throws Exception{
        return (mapper.getTotalPage(num)-1)/10+1;
    }

    public ListForm findBoardByIdx(int idx) throws Exception{
        mapper.increaseView(idx); //조회수증가
        return mapper.findBoardByIdx(idx);
    }

    public boolean increaseLike(int user_idx, int board_idx) throws Exception {
        //좋아요 안 한게 맞는지?
        if (mapper.checkLike(user_idx, board_idx) != 0) {
            return false;
        } else {
            int affectedRows = mapper.increaseLike(user_idx, board_idx);
            if (affectedRows > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

}
