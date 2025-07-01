package hello.backendproject.board.searchlog.service;

import hello.backendproject.board.searchlog.domain.SearchLogDocument;
import hello.backendproject.board.searchlog.repository.SearchLogEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 엘라스틱 서치 저장, 통게 집계 비지니스 로직 서비스
public class SearchLogEsService {

    private final SearchLogEsRepository searchLogEsRepository;

    // 카프카에서 전달 받은 검색 데이터 저장 메서드
    public void save(SearchLogDocument searchLogDocument) {
        searchLogEsRepository.save(searchLogDocument);
    }
}
