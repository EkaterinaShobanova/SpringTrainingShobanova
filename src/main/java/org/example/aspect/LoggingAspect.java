package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* org.example.service.TaskService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Before: Method {} called with args: {}",
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }


    @AfterThrowing(
            pointcut = "execution(* org.example.service.*.*(..)) || " +
                    "execution(* org.example.kafka.*.*(..))",
            throwing = "ex"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Exception in {}: {} - {}",
                joinPoint.getSignature().toShortString(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
    }

    // Логируем только публичные методы контроллеров
    @AfterReturning(
            pointcut = "execution(public * org.example.controller.*.*(..))",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("Controller {} returned: {}",
                    joinPoint.getSignature().getName(),
                    result != null ? result.toString() : "null");
        }
    }

    // Замеряем время выполнения только бизнес-методов
    @Around("execution(* org.example.service.*Service.*(..)) || " +
            "execution(* org.example.repository.*Repository.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Method {} executed in {} ms",
                        joinPoint.getSignature().toShortString(),
                        System.currentTimeMillis() - startTime);
            }
            return result;
        } catch (Throwable ex) {
            log.error("Execution failed in {} after {} ms",
                    joinPoint.getSignature().toShortString(),
                    System.currentTimeMillis() - startTime);
            throw ex;
        }
    }
}
