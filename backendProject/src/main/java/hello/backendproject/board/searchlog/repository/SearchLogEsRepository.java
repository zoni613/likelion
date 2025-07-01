package hello.backendproject.board.searchlog.repository;

import hello.backendproject.board.searchlog.domain.SearchLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchLogEsRepository extends ElasticsearchRepository<SearchLogDocument, String> {

    // 엘라스틱 서치 저장/검색용 레포지토리
}
