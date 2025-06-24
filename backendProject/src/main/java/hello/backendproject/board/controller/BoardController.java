package hello.backendproject.board.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.backendproject.board.dto.BoardDTO;
import hello.backendproject.board.service.BoardService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 글 작성
     **/
    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@RequestBody BoardDTO boardDTO) throws JsonProcessingException {
        System.out.println("boardDTO 값 " + new ObjectMapper().writeValueAsString(boardDTO));
        BoardDTO created = boardService.createBoard(boardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 게시글 상세 조회
     **/
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardDetail(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardDetail(id));
    }

    /**
     * 게시글 수정
     **/
    @PutMapping("/{id}")
    public ResponseEntity<BoardDTO> updateBoard(@PathVariable Long id, @RequestBody BoardDTO boardDTO) {
        return ResponseEntity.ok(boardService.updateBoard(id, boardDTO));
    }

    /**
     * 게시글 삭제
     **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

    //페이징 적용 전
    @GetMapping
    public ResponseEntity<List<BoardDTO>> getBoardList() {
        return ResponseEntity.ok(boardService.getBoardList());
    }

    //페이징 적용 전
    @GetMapping("/search")
    public List<BoardDTO> search(@RequestParam String keyword) {
        return boardService.searchBoards(keyword);
    }

    /** 페이징 적용 **/
    /** 페이징 적용 **/
    /** 페이징 적용 **/
    /** 페이징 적용 **/
    //페이징 적용 전체 목록보기
    //기본값은 0페이지 첫페이지입니다 페이지랑 10개 데이터를 불러옴
//    @GetMapping
//    public Page<BoardDTO> getBoards(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return boardService.getBoards(page, size);
//    }

    /** 주석해제하기 front  **/
    //페이징 적용 검색
//    @GetMapping("/search")
//    public Page<BoardDTO> search(
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return boardService.searchBoardsPage(keyword, page, size);
//    }

    /**
     * 게시판 글 쓰기 배치 작업
     **/
    @PostMapping("/batchInsert")
    public String batchInsert(@RequestBody List<BoardDTO> boardDTOList) {
        boardService.batchSaveBoard(boardDTOList);
        return "ok";
    }
}
