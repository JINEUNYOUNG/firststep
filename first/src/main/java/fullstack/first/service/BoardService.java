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

    public int getTotalPage(int num) throws Exception{
        return (mapper.getTotalPage(num)-1)/10+1;
    }

}
