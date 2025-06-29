package hello.backendproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice // 스프링에서 모든 컨트롤러의 예외를 한 곳에서 처리하기 위한 어노테이션
public class GlobalExceptionHandler {

    // 400: 파라미터 타입 오류, JSON 파싱 오류 등
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception e) {
        log.warn("[BAD_REQUEST] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, "잘못된 요청입니다.", e.getMessage()));
    }

    // 400: DTO validation(@Valid) 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[VALIDATION_FAIL] {}", e.getMessage());

        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("유효성 검사 실패");

        ErrorResponse error = new ErrorResponse(400, "요청 값이 유효하지 않습니다.", detail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 400: 잘못된 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[ILLEGAL_ARGUMENT] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "잘못된 요청입니다.", e.getMessage()));
    }

    // 404: 리소스 없음
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElement(NoSuchElementException e) {
        log.warn("[NOT_FOUND] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "리소스를 찾을 수 없습니다.", e.getMessage()));
    }

    // 400: ModelAttribute 바인딩 실패
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<?> handleBindException(org.springframework.validation.BindException e) {
        log.warn("[BIND_ERROR] {}", e.getMessage());
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "요청 값이 유효하지 않습니다.", detail));
    }

    // 인증 실패: 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        log.warn("[LOGIN_FAIL] {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(401, "인증에 실패했습니다.", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 인가 실패: 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        log.warn("[ACCESS_DENIED] {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(403, "권한이 없습니다.", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 그 외 모든 예외: 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("[EXCEPTION][UNHANDLED] {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(500, "서버 내부 오류가 발생했습니다.", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
