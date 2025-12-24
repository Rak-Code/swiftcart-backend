package com.ecommerce.project.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging execution of service and controller methods.
 * Uses async logging via Logback configuration.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Log execution time of controller methods
     */
    @Around("execution(* com.ecommerce.project.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        
        log.debug("Entering controller method: {}", methodName);
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("Completed controller method: {} in {}ms", methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Exception in controller method: {} after {}ms - {}", 
                     methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Log execution time of service methods
     */
    @Around("execution(* com.ecommerce.project.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        
        log.debug("Entering service method: {}", methodName);
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (executionTime > 1000) {
                log.warn("Slow service method: {} took {}ms", methodName, executionTime);
            } else {
                log.debug("Completed service method: {} in {}ms", methodName, executionTime);
            }
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Exception in service method: {} after {}ms - {}", 
                     methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}
