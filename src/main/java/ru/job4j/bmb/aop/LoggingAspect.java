package ru.job4j.bmb.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* ru.job4j.bmb.services.*.*(..))")
    private void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        LOGGER.info("");
        LOGGER.info("------ Вызов метода: {} ------", joinPoint.getSignature().getName());
        int i = 1;
        for (Object s : joinPoint.getArgs()) {
            LOGGER.info("Значение параметра {}: {}", i++, s);
        }
    }

    @AfterReturning(value = "serviceLayer()", returning = "result")
    public void logAfter(Object result) {
        if (result != null) {
            LOGGER.info("Значение возвращаемого параметра: {}", result);
        }
    }
}

