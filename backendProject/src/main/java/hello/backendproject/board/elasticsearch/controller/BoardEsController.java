package hello.backendproject.board.elasticsearch.controller;

import hello.backendproject.board.elasticsearch.dto.BoardEsDocument;
import hello.backendproject.board.elasticsearch.service.BoardEsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/boards")
public class BoardEsController {
    private final BoardEsService boardEsService;

    // 엘라스틱서치 검색 결과를 page 형태로 감싼 다음에 HTTP 응답을 json으로 반환
    @GetMapping("/elasticsearch")
    public ResponseEntity<Page<BoardEsDocument>> elasticSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(boardEsService.search(keyword, page, size));
    }
}
