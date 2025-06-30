package hello.backendproject.board.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import hello.backendproject.board.elasticsearch.dto.BoardEsDocument;
import hello.backendproject.board.elasticsearch.repository.BoardEsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardEsService {

    // Elastic search에 명령을 전달하는 서버 API
    private final ElasticsearchClient client;

    private final BoardEsRepository repository;

    // 데이터 저장 메서드
    public void save(BoardEsDocument document) {
        repository.save(document);
    }

    // 데이터 삭제 메서드
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    // 검색 키워드와 페이지 번호와 크기를 받아 엘라스틱서치에서 검색하는 메서드
    // 검색된 정보와 페이징 정보도 함께 반환하도록 하기 위해 Page 객체를 사용하여 반환
    public Page<BoardEsDocument> search(String keyword, int page, int size) {
        try {
            // 엘라스틱서치에서 페이징을 위한 시작 위치를 계산하는 변수
            int from = page * size;

            // 엘라스틱서치에서 사용할 검색조건을 담는 객체
            Query query;

            // 검색 키워드가 없으면 모든 문서를 검색하는 matchAll 쿼리
            if (keyword == null || keyword.isBlank()) {
                query = MatchAllQuery.of(m -> m)._toQuery(); // 전체 문서를 가져오는 쿼리를 생성하는 람다 함수
                // MatchAllQuery는 엘라스틱서치에서 조건 없이 모든 문서를 검색할 때 사용하는 쿼리
            }
            // 검색어가 있을 때
            else {
                // boolquery는 복수 조건을 조합할 때 사용하는 쿼리
                // 이 쿼리 안에서 여러개의 조건을 나열
                // 예를 들어서 백엔드라는 키워드가 들어왔을 때 이 백엔드 키워드를 어떻게 분석해 데이터를 보여줄 것인가 작성
                query = BoolQuery.of(b -> {

                    // PrefixQuery는 해당 필드가 특정 단어로 시작하는지 검사하는 쿼리
                    // MatchQuery는 해당 단어가 포함되어 있는지 검사하는 쿼리

                    /**
                     * must: 모두 일치해야 함 (AND)
                     * should: 하나라도 일치하면 됨 (OR)
                     * must_not: 해당 조건을 맞고하면 제외
                     * filter : must와 같지만 점수 계산 안함 (속도가 빠름)
                     */
                    
                    // 접두어 글자 검색
                    b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());
                    b.should(PrefixQuery.of(p -> p.field("content").value(keyword))._toQuery());

                    // 초성 검색
                    b.should(PrefixQuery.of(p -> p.field("title.chosung").value(keyword))._toQuery());
                    b.should(PrefixQuery.of(p -> p.field("content.chosung").value(keyword))._toQuery());

                    // 중간 문자 검색 (match만 가능)
                    b.should(MatchQuery.of(p -> p.field("title.ngram").query(keyword))._toQuery());
                    b.should(MatchQuery.of(p -> p.field("content.ngram").query(keyword))._toQuery());

                    // fuzziness: "AUTO"는  오타 허용 검색 기능을 자동으로 켜주는 설정 -> 유사도 계산을 매번 수행하기 때문에 느림
                    //짧은 키워드에는 사용 xxx
                    //오타 허용 (오타허용은 match만 가능 )
                    if (keyword.length()>=3){
                        b.should(MatchQuery.of(m ->m.field("title").query(keyword).fuzziness("AUTO"))._toQuery());
                        b.should(MatchQuery.of(m ->m.field("content").query(keyword).fuzziness("AUTO"))._toQuery());
                    }

                    return b;
                })._toQuery();
            }

            // SearchRequest는 엘라스틱서치에서 검색을 하기 위한 검색요청 객체
            // 인덱스명, 페이징 정보, 쿼리를 포함한 검색 요청
            SearchRequest request = SearchRequest.of(s -> s
                    .index("board-index")
                    .from(from)
                    .size(size)
                    .query(query)
            );

            // SearchResponse는 엘라스틱서치의 검색 결과를 담고 있는 응답 객체
            SearchResponse<BoardEsDocument> response =
                    // 엘라스틱서치에 명령을 전달하는 자바 API 검색요청을 담아서 응답객체로 반환
                    client.search(request, BoardEsDocument.class);

            // 위 응답객체에서 받은 검색 결과 중 문서만 추출해서 리스트로 만들어줌
            // Hit는 엘라스틱서치에서 검색된 문서 1개를 감싸고 있는 객체
            List<BoardEsDocument> content = response.hits() // 엘라스틱서치 응답에서 hits(문서 검색결과) 전체를 꺼냄
                    .hits() // 검색 결과 안에 개별 리스트를 가져옴
                    .stream() // JAVA stream api를 사용
                    .map(Hit::source) // 각 Hit 객체에서 실제 문서를 꺼내는 작업
                    .collect(Collectors.toList()); // 위에서 꺼낸 객체를 JAVA List에 넣는다

            // 전체 검색 결과 수 (총 문서의 갯수)
            long total = response.hits().total().value();

            // PageImpl 객체를 사용해서 Spring에서 사용할 수 있는 page 객체로 변환
            return new PageImpl<>(content, PageRequest.of(page, size), total);

        } catch (Exception e) {
            log.error("검색 오류", e);
            throw new RuntimeException("검색 중 오류 발생", e);
        }
    }
    
    // 문서 리스트를 받아서 엘라스틱서치에 bulk색인하는 메서드
    public void bulkIndexInsert(List<BoardEsDocument> documents) throws IOException {
        // 한 번에 처리할 묶음(batch) 크기를 설정
        int batchSize = 1000;

        for (int i = 0; i < documents.size(); i+=batchSize) {
            // 현재 batch의 끝 인덱스를 구함
            int end = Math.min(documents.size(), i + batchSize);

            // 현재 batch 단위의 문서 리스트를 잘라냄
            List<BoardEsDocument> batch = documents.subList(i, end);

            // 엘라스틱 서치의 bulk 요청을 담을 빌더 생성
            BulkRequest.Builder br = new BulkRequest.Builder();

            // 각 문서를 bulk  요청 안에 하나씩 담음
            for (BoardEsDocument document : batch) {
                br.operations(op -> op // operations()로 하나하나 문서를 담음
                        .index(idx -> idx // 인덱스에 문서를 저장하는 작업
                                .index("board-index") // 인덱스명
                                .id(String.valueOf(document.getId())) // 수동으로 Id 지정
                                .document(document) // 실제 저장할 문서 객체
                        )
                );
            }

            // bulk 요청 실행 : batch 단위로 엘라스틱서치에 색인 수행
            BulkResponse response = client.bulk(br.build());

            // 벌크 작업 중 에러가 있는 경우 로그 출력
            if(response.errors()) {
                for(BulkResponseItem item: response.items()) {
                    if(item.error() != null) {
                        // 실패한 문서의 ID와 여러 내용을 출력
                        log.error("엘라스틱서치 벌크 색인 작업 중 오류 실패 {}, 오류: {}", item.id(), item.error());
                    }
                }
            }
        }
    }
}
