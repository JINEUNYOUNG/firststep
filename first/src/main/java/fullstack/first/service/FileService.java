package fullstack.first.service;

import fullstack.first.mapper.fileMapper;
import fullstack.first.vo.FileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {

    @Autowired
    public fileMapper mapper;

    //파일 실제 물리저장 메서드
    public void saveFile(MultipartFile file, String file_name) throws Exception {
        try {
            String savePath = System.getProperty("user.dir") + "\\upload"; //사용자의 루트경로의 upload파일
            if (!new File(savePath).exists()) {
                try {
                    new File(savePath).mkdirs();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            String filePath = savePath + "\\" + file_name;
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //파일 db저장
    public int addFile(int board_idx, List<MultipartFile> file) throws Exception {
        int result = 0;
        //File객체에 매핑
        for (MultipartFile x : file) {
            if (!file.isEmpty()) {
                FileVO files = new FileVO();
                files.setFile_extension(x.getOriginalFilename().split("\\.")[1]);
                files.setOriginal_name(x.getOriginalFilename());
                files.setFile_size((int) x.getSize());
                files.setFile_name(UUID.randomUUID() + "." + x.getOriginalFilename().split("\\.")[1]);
                saveFile(x, files.getFile_name()); //실제 파일 저장
                if (mapper.addFile(board_idx, files) == 1) { //db에 저장
                    result++;
                }
            }
        }
        if (result == file.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    public List<FileVO> getFilesByIdx(int board_idx) throws Exception {
        return mapper.getFilesByIdx(board_idx);
    }

    public FileVO getFileByIdx(int file_idx) throws Exception {
        return mapper.getFileByIdx(file_idx);
    }

    public byte[] getFileData(FileVO file) throws Exception {
        String savePath = System.getProperty("user.dir") + "\\upload\\";
        Path filePath = Paths.get(savePath+file.getFile_name());
        System.out.println(filePath);
        return Files.readAllBytes(filePath);
    }

}
