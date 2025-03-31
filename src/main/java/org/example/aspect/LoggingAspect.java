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
            pointcut = "execution(* org.example.*.*(..))",
            throwing = "ex"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("AfterThrowing: Method {} threw exception: {}",
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }


    @AfterReturning(
            pointcut = "execution(* org.example.*.*(..))",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("AfterReturning: Method {} returned: {}",
                joinPoint.getSignature().getName(),
                result);
    }


    @Around("execution(* org.example.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Around: Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                duration);
        return result;
    }

}
