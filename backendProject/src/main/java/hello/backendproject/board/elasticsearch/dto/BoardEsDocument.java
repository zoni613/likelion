package hello.backendproject.board.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hello.backendproject.board.dto.BoardDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties(ignoreUnknown = true) // 해당 설정을 넣지 않으면 class 속성이 들어가게 됨
@Document(indexName = "board-index")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEsDocument {
    @Id
    private String id;
    private String title;
    private String content;
    private String username;
    private Long userId;
    private String create_date;
    private String update_date;

    public static BoardEsDocument from(BoardDTO dto) {
        // BoardDTO를 받아서 엘라스틱서치 DTO를 변환
        return BoardEsDocument.builder()
                .id(String.valueOf(dto.getId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .username(dto.getUsername())
                .userId(dto.getUser_id())
                .create_date(dto.getCreated_date() != null ? dto.getCreated_date().toString() : null)
                .update_date(dto.getUpdated_date() != null ? dto.getUpdated_date().toString() : null)
                .build();
    }

}
