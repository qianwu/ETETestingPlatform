//package org.charlotte.e2ecore.aspect;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.core.convert.ConversionService;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import javax.xml.soap.MimeHeaders;
//import java.lang.reflect.Field;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
//@Aspect
//@Component
//@Slf4j
//public class ControllerCommonAspect {
//
//    //org.charlotte.e2ecore.endpoint.CaseController
//    @Pointcut("execution(* org.charlotte.e2ecore.endpoint..*(..))")
//    public void controllerCommonAspect() {
//    }
//
//    @Around("controllerCommonAspect()")
//    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        System.out.println("proceedingJoinPoint:");
//        System.out.println(proceedingJoinPoint);
//
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//        System.out.println("request:");
//        System.out.println(request);
//        Object[] requestArr = proceedingJoinPoint.getArgs();
//        StringBuilder sb = new StringBuilder();
//        for (Object requestItem : requestArr) {
//            sb.append(requestItem.toString());
//            sb.append(StringUtils.SPACE);
//        }
//        log.info("method:{} url:{} request:{}", request.getMethod(), request.getRequestURI(), sb);
//        return proceedingJoinPoint.proceed();
//    }
//
//}
