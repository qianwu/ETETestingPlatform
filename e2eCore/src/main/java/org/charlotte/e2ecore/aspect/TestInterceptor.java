//package org.charlotte.e2ecore.aspect
//
//import org.charlotte.e2ecore.utils.UUIDUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.MDC;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Objects;
//
///**
// * @author ：charlotte
// * @date ：Created in 15/2/22 2:49 PM
// * @description ：
// */
//@Slf4j
//public class TestInterceptor implements HandlerInterceptor {
//    private final static String ATTRIBUTE_TRACE_ID_KEY = "ATTRIBUTE_TRACE_ID_KEY";
//    private final static String TRACE_PARENT_KEY = "traceparent";
//    private final static String MDC_TRACE_ID_KEY = "trace_id";
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        String traceId = this.getTraceId();
//        log.info("traceId={}", traceId);
//        setHeader(request, traceId);
//        MDC.put(MDC_TRACE_ID_KEY, traceId);
//        return true;
//    }
//
//    public void setHeader(HttpServletRequest request, String traceId) {
//        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
//
//            @Override
//            public String getHeader(String name) {
//                String superHeader = super.getHeader(name);
//                if ("traceparent".equals(name) && StringUtils.isEmpty(superHeader)) {
//                    String spanId = UUIDUtil.getUUID16();
//                    return String.join("-", "00", traceId, spanId, "01");
//                }
//                return superHeader;
//            }
//        };
//    }
//
//    private String getTraceId() {
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//
//        String traceParent = request.getHeader(TRACE_PARENT_KEY);
//        if (Objects.nonNull(traceParent)) {
//            String[] traceParentSplit = traceParent.split("-");
//            return traceParentSplit[1];
//        }
//
//        String trace_id = MDC.get(MDC_TRACE_ID_KEY);
//        if (Objects.nonNull(trace_id)){
//            return trace_id;
//        }
//
//        return UUIDUtil.getUUID32();
//    }
//}
