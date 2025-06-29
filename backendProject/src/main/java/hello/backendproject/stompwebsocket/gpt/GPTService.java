package hello.backendproject.stompwebsocket.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class GPTService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;
    //json문자열 <-> 자바객체, json객체
    private final ObjectMapper mapper = new ObjectMapper();

    public String gptMessage(String message) throws Exception{

        //API 호출을 위한 본문 작성
        Map<String,Object> requestBody  = new HashMap<>();
        requestBody.put("model","o3");
        requestBody.put("input",message);

        //http 요청 작성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization",apiKey)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))) //본문 삽입
                .build();

        //요청 전송 및 응답 수신
        HttpClient  client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        //응답을 Json으로 파싱
        JsonNode jsonNode = mapper.readTree(response.body());
        log.info("gpt 응답 : "+jsonNode);

        //메세지 부분만 추출하여 반환
        String gptMessageResponse = jsonNode.get("output").get(1).get("content").get(0).get("text").asText();
        return gptMessageResponse;

    }

}