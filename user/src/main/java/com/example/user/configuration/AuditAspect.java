package com.example.user.configuration;

import com.example.user.services.AuditService;
import com.example.user.services.Identifiable;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditService auditService;

    @AfterReturning(pointcut = "execution(* com.example.user.controllers.*.*(..))", returning = "result")
    public void logAction(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String action = methodName.toUpperCase(); // Ex: "CREATE", "UPDATE", "DELETE"
        String details = "Action performed: " + methodName;

        auditService.logAction(username, action);
    }

    private Long extractEntityId(Object result) {
        // Implémentez la logique pour extraire l'ID de l'entité
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) result).getBody();
            if (body != null && body instanceof Identifiable) {
                return ((Identifiable) body).getId();
            }
        }
        return null;
    }
}