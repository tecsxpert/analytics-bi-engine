package com.internship.tool.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.repository.AuditLogRepository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditLogRepository auditRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Around("execution(* com.internship.tool.service.AnalyticsRecordService.save(..))")
    public Object auditSave(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        String newData = mapper.writeValueAsString(args[0]);

        Object result = joinPoint.proceed();

        AuditLog log = new AuditLog();

        log.setAction("SAVE");
        log.setOldData("N/A");
        log.setNewData(newData);

        auditRepository.save(log);

        return result;
    }
}