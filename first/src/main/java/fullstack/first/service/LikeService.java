package fullstack.first.service;

import fullstack.first.mapper.likeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeService {

    @Autowired
    public likeMapper mapper;

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
