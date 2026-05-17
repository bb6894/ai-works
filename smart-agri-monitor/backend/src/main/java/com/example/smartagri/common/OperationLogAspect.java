package com.example.smartagri.common;

import com.example.smartagri.domain.OperationLog;
import com.example.smartagri.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    private final OperationLogMapper operationLogMapper;

    @Around("@annotation(operationLoggable)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLoggable operationLoggable) throws Throwable {
        OperationLog log = new OperationLog();
        log.setUserId(CurrentUser.id());
        log.setUsername(CurrentUser.username());
        log.setModuleName(operationLoggable.module());
        log.setActionName(operationLoggable.action());
        log.setCreatedAt(LocalDateTime.now());
        applyRequest(log);
        try {
            Object result = joinPoint.proceed();
            log.setSuccess(1);
            return result;
        } catch (Throwable ex) {
            log.setSuccess(0);
            log.setErrorMessage(ex.getMessage());
            throw ex;
        } finally {
            operationLogMapper.insert(log);
        }
    }

    private void applyRequest(OperationLog log) {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            log.setRequestMethod(request.getMethod());
            log.setRequestUri(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
        }
    }
}
