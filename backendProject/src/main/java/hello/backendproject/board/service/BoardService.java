package hello.backendproject.board.service;

import hello.backendproject.board.dto.BoardDTO;
import hello.backendproject.board.elasticsearch.dto.BoardEsDocument;
import hello.backendproject.board.elasticsearch.repository.BoardEsRepository;
import hello.backendproject.board.elasticsearch.service.BoardEsService;
import hello.backendproject.board.entity.Board;
import hello.backendproject.board.repository.BatchRepository;
import hello.backendproject.board.repository.BoardRepository;
import hello.backendproject.user.entity.User;
import hello.backendproject.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepositoty;
    private final EntityManager em;

    // 엘라스틱서치 Service
    private final BoardEsService boardEsService;
    private final BoardEsRepository boardEsRepository;

    /**
     * 글 등록
     **/
    @Transactional
    public BoardDTO createBoard(BoardDTO boardDTO) {

        // userId(PK)를 이용해서 User 조회
        if (boardDTO.getUser_id() == null)
            throw new IllegalArgumentException("userId(PK)가 필요합니다!");

        // 연관관계 매핑!
        // 작성자 User 엔티티 조회 (userId 필요)
        User user = userRepository.findById(boardDTO.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보가 올바르지 않습니다."));

        /** mysql 저장 **/
        Board board = new Board();
        board.setTitle(boardDTO.getTitle());
        board.setContent(boardDTO.getContent());
        // 연관관계 매핑!
        board.setUser(user);
        Board saved = boardRepository.save(board);
        // ======= mysql 저장 완료 =======

        // ======= elasticSearch 저장 시작 =======
        BoardEsDocument doc = BoardEsDocument.builder()
                .id(String.valueOf(board.getId()))
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getId())
                .username(board.getUser().getUserProfile().getUsername())
                .create_date(String.valueOf(board.getCreated_date()))
                .update_date(String.valueOf(board.getUpdated_date()))
                .build();
        boardEsService.save(doc);

        return toDTO(saved);
    }


    /**
     * 게시글 상세 조회
     **/
//    @Transactional(readOnly = true)
    @Transactional
    public BoardDTO getBoardDetail(Long boardId, Long userId) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("로그인 후 이용해주세요.");
        }
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));

        // mysql 조회수 증가
        board.setViewCount(board.getViewCount() + 1);

        // es 조회수 증가
        BoardEsDocument esDocument = boardEsRepository.findById(String.valueOf(boardId))
                .orElseThrow(() -> new IllegalArgumentException("ES에 게시글 없음 : " + boardId));

        esDocument.setViewCount(board.getViewCount());
        boardEsService.save(esDocument);

        return toDTO(board);
    }

    /**
     * 게시글 수정
     **/
    @Transactional
    public BoardDTO updateBoard(Long boardId, Long userId, BoardDTO dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));

        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        boardRepository.save(board); //  해당 코드는 필요 없을 수도 있음. 영속성 안 entity가 변경됐기 때문
        // ======= mysql 수정 완료 =======

        // ======= elasticSearch 수정 시작 =======
        BoardEsDocument doc = BoardEsDocument.builder()
                .id(String.valueOf(board.getId()))
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getId())
                .username(board.getUser().getUserProfile().getUsername())
                .create_date(String.valueOf(board.getCreated_date()))
                .update_date(String.valueOf(board.getUpdated_date()))
                .build();
        boardEsService.save(doc);

        return toDTO(board);
    }

    /**
     * 게시글 삭제
     **/
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
//        if (!boardRepository.existsById(boardId))
//            throw new IllegalArgumentException("게시글 없음: " + boardId);

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + boardId));
        if (!board.getUser().getId().equals(userId))
            throw new IllegalArgumentException("삭제 권한이 없습니다.");

        // mysql 삭제
        boardRepository.deleteById(boardId);

        // 엘라스틱 서치 삭제 추가
        boardEsRepository.deleteById(String.valueOf(boardId));
    }

    /** 페이징 적용 전 **/
    /** 페이징 적용 전 **/
    /**
     * 페이징 적용 전
     **/
    // 게시글 전체 목록
    @Transactional(readOnly = true)
    public List<BoardDTO> getBoardList() {
        return boardRepository.findAll().stream()
                .map(this::toDTO) // => .map((board) -> toDTO(board))
                .collect(Collectors.toList());
    }

    // 게시글 검색  페이징 아님
    public List<BoardDTO> searchBoards(String keyword) {
        return boardRepository.searchKeyword(keyword);
    }

    /** 페이징 적용 후 **/
    /** 페이징 적용 후 **/
    /**
     * 페이징 적용 후
     **/
    //페이징 전체 목록
    public Page<BoardDTO> getBoards(int page, int size) {
        return boardRepository.findAllPaging(PageRequest.of(page, size)); //페이저블에 페이징에대한 정보를 담아서 레포지토리에 전달하는 역할
        //    return boardRepository.findAllWithDto(PageRequest.of(page, size, Sort.by("id").ascending())); //함수로 정렬
    }

    //페이징 검색 목록
    public Page<BoardDTO> searchBoardsPage(String keyword, int page, int size) {
        return boardRepository.searchKeywordPaging(keyword, PageRequest.of(page, size));
    }

    // Entity → DTO 변환
    private BoardDTO toDTO(Board board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());

        dto.setUser_id(board.getUser().getId());
        dto.setUsername(board.getUser() != null ? board.getUser().getUserProfile().getUsername() : null); // ★ username!

        dto.setCreated_date(board.getCreated_date());
        dto.setUpdated_date(board.getUpdated_date());
        dto.setViewCount(board.getViewCount());
        return dto;
    }

    /**
     * 배치작업 - JDBC Template
     **/
    @Transactional
    public void batchSaveBoard(List<BoardDTO> boardDTOList) {
        Long start = System.currentTimeMillis();

        int batchsize = 1000; //한번에 처리할 배치 크기
        for (int i = 0; i < boardDTOList.size(); i += batchsize) { //i는 1000씩 증가
            //전체 데이터를 1000개씩 잘라서 배치리스트에 담습니다.

            int end = Math.min(boardDTOList.size(), i + batchsize); //두개의 숫자중에 작은 숫자를 반환
            List<BoardDTO> batchList = boardDTOList.subList(i, end);

            //전체 데이터에서 1000씩 작업을 하는데 마지막 데이터가 1000개가 안될수도있으니
            //Math.min()으로 전체 크기를 넘지 않게 마지막 인덱스를 계산해서 작업합니다.


            //내가 넣은 데이터만 엘라스틱서치에 동기화하기 위해 uuid 생성
            String batchKey = UUID.randomUUID().toString();
            for (BoardDTO dto : batchList) {
                dto.setBatchkey(batchKey);
            }

            // 1. MySQL로 INSERT
            batchRepositoty.batchInsert(batchList);

            // 2. Mysql에 insert한 데이터를 다시 조회
            List<BoardDTO> saveBoards = batchRepositoty.findByBatchKey(batchKey);

            // 3. 엘라스틱서치용으로 변환
            List<BoardEsDocument> documents = saveBoards.stream()
                    .map(BoardEsDocument::from) // DTO -> 엘라스틱서치용 DTO로 변환
                    .toList();

            try {
                // 4. 엘라스틱서치 bulk 인덱싱
                boardEsService.bulkIndexInsert(documents);
            }
            catch (IOException e) {
                log.error("[BOARD][BATCH] ElasticSearch 벌크 인덱싱 실해: {}", e.getMessage());
            }

        }

        Long end = System.currentTimeMillis();
        log.info("[BOARD][BATCH] 전체 저장 소요 시간(ms): {}", (end - start));
    }

    @Transactional
    public void boardSaveAll(List<Board> boardList) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < boardList.size(); i++) {
            em.persist(boardList.get(i));
            if (i % 1000 == 0) {
                em.flush();
                em.clear();
            }
        }

        long end = System.currentTimeMillis();
        log.info("JPA Board saveAll 저장 소요 시간(ms): " + (end - start));
    }
}

