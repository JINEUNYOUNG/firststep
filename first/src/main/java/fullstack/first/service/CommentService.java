package fullstack.first.service;

import fullstack.first.mapper.boardMapper;
import fullstack.first.mapper.commentMapper;
import fullstack.first.vo.Comment;
import fullstack.first.vo.CommentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    @Autowired
    public commentMapper mapper;

    public boolean addComment(Comment comment) throws Exception{
        return mapper.addComment(comment.getBoard_idx(), comment.getComment_content(), comment.getUser_idx()) == 1;
    }

    public List<CommentForm> findCommentByIdx(int idx) throws Exception{
        return mapper.findCommentByIdx(idx);
    }

}
