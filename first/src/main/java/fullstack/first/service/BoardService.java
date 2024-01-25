package fullstack.first.service;

import fullstack.first.mapper.boardMapper;
import fullstack.first.vo.Board;
import fullstack.first.vo.ListForm;
import fullstack.first.vo.WriteForm;
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
        page = (page - 1) * 10;
        return mapper.getList(num, page);
    }

    public List<ListForm> getNotice(int num) throws Exception {
        return mapper.getNotice(num);
    }

    public int getTotalPage(int num) throws Exception {
        return (mapper.getTotalPage(num) - 1) / 10 + 1;
    }

    public ListForm findBoardByIdx(int idx) throws Exception {
        mapper.increaseView(idx); //조회수증가
        return mapper.findBoardByIdx(idx);
    }

    public int deleteBoard(int board_idx, int user_idx) throws Exception {
        //작성자거나 관리자가 맞는지 확인하고 삭제
        int writer = mapper.getBoardWriter(board_idx);
        if ((writer == user_idx)||(user_idx == 1)) {
            mapper.deleteBoard(board_idx);
            return 1;
        } else {
            return 0;
        }
    }
    public void setNotice(int board_idx) throws Exception {
        mapper.setNotice(board_idx);
    }
    public void cancelNotice(int board_idx) throws Exception {
        mapper.cancelNotice(board_idx);
    }

    //게시물 insert 후에 idx를 반환
    public int writeBoard(WriteForm writeForm) throws Exception {
        mapper.writeBoard(writeForm);
        return mapper.getBoardIdx();
    }


}
