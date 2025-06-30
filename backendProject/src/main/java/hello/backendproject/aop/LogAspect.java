package hello.backendproject.aop;

import hello.backendproject.threadlocal.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Aspect // 공통으로 관리하고 싶은 기능을 담당하는 클래스에 붙이는 어노테이션
public class LogAspect {

    // AOP를 적용할 클래스
    @Pointcut(
            "execution(* hello.backendproject.board.service..*(..)) || " +
            "execution(* hello.backendproject.user.service..*(..)) || " +
            "execution(* hello.backendproject.auth.service..*(..)) || " +
            "execution(* hello.backendproject.comment.service..*(..))"
    )
    // "execution(* hello.backendproject.comment.service..*(..))"
    public void method() {}

    // PointCut
    @Around("method()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();

        try {
            log.info("[AOP_LOG][TraceId]{} {} 메서드 호출 시작", TraceIdHolder.get(), methodName);
            Object result = joinPoint.proceed(); // JoinPoint
            return result;
        }
        catch (Exception e) {
            log.error("[AOP_LOG][TraceId]{} {} 메서드 예외 {}", TraceIdHolder.get(), methodName, e.getMessage());
            return e;
        }
        finally {
            long end =  System.currentTimeMillis();
            log.info("[AOP_LOG][TraceId]{} {} 메서드 실행 완료 시간 = {} ms]", TraceIdHolder.get(), methodName, end - start);
        }
    }

    /*
    // @Before aop가 실행되기 직저네에 호출
//    @Before("execution(* hello.backendproject.board.service..*(..))")
    @Before("method()")
    public void beforeLog(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[AOP_LOG][START] {} 메서드 호출 시작", methodName);
    }
    
    // @After aop가 실행된 직후에 호출
//    @After("execution(* hello.backendproject.board.service..*(..))")
    @After("method()")
    public void afterLog(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("[AOP_LOG][END] 메서드 = {} 호출 종료", methodName);
    }
     */
}
