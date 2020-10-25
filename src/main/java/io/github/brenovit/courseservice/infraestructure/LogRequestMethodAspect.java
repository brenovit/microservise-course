package io.github.brenovit.courseservice.infraestructure;

import static net.logstash.logback.argument.StructuredArguments.entries;
import static net.logstash.logback.argument.StructuredArguments.kv;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Aspect
@Configuration
public class LogRequestMethodAspect {

    private static final String START_MESSAGE = "Started Endpoint Processing";
    private static final String END_MESSAGE = "Finished Endpoint Processing";
    private static final String ERROR_MESSAGE = "Failure on Endpoint Processing";
    private static final String START = "START";
    private static final String END = "END";

    private static final String ARGUMENT_ACTION = "action";
    private static final String ARGUMENT_HTTP_STATUS = "status";
    private static final String ARGUMENT_METHOD = "method";
    private static final String ARGUMENT_PHASE = "phase";
    private static final String ARGUMENT_RESPONSE = "response";
    private static final String ARGUMENT_REQUEST_BODY = "requestBody";
    private static final String ARGUMENT_RESPONSE_BODY = "response-body";
    private static final String ARGUMENT_PATH = "path";
    private static final String ARGUMENT_PATH_PARAMETERS = "pathParameters";
    private static final String ARGUMENT_REQUEST = "request";
    private static final String ARGUMENT_SUCCESS = "success";
    private static final String ARGUMENT_QUERY_PARAMETERS = "queryParameters";

    /**
     * Surround every LogRequestMethod annotation including the Logging Application EndPoints capacity.
     * Provides dynamic and customized Log for Request Mappings.
     *
     * @param joinPoint Surrounded Method
     * @return Surrounded Method Return Object
     * @throws Throwable Surrounded Method Original Trowed Exception
     */
    @Around("@annotation(io.github.brenovit.courseservice.infraestructure.LogRequestMethod)")
    public Object generateRequestMappingLog(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequestMapping requestMappingAnnotation = signature.getMethod().getAnnotation(RequestMapping.class);
        LogRequestMethod logRequestAnnotation = signature.getMethod().getAnnotation(LogRequestMethod.class);

        @SuppressWarnings("squid:S1312")
        final Logger LOGGER = LoggerFactory.getLogger(signature.getDeclaringType());

        Map<String, Object> jsonArguments =
                buildJsonArguments(joinPoint, signature, logRequestAnnotation, requestMappingAnnotation);

        try {

            LOGGER.info(START_MESSAGE, entries(jsonArguments));

            Object methodExecutionReturn = joinPoint.proceed();

            jsonArguments.put(ARGUMENT_PHASE, END);
            jsonArguments.put(ARGUMENT_RESPONSE, buildResponse(methodExecutionReturn, logRequestAnnotation));
            LOGGER.info(END_MESSAGE, entries(jsonArguments), kv(ARGUMENT_SUCCESS, true));

            return methodExecutionReturn;

        } catch(Exception e) {

            jsonArguments.put(ARGUMENT_PHASE, END);
            LOGGER.error(ERROR_MESSAGE, entries(jsonArguments), kv(ARGUMENT_SUCCESS, false), e);
            throw e;
        }
    }

    private Map<String, Object> buildResponse(Object responseObject, LogRequestMethod logRequestAnnotation) {

        Map<String, Object> responseArguments = new HashMap<>();
        Object responseBody = responseObject;

        if (responseObject instanceof ResponseEntity) {

            ResponseEntity responseEntity = (ResponseEntity) responseObject;
            responseArguments.put(ARGUMENT_HTTP_STATUS, String.valueOf(responseEntity.getStatusCodeValue()));

            if (responseEntity.hasBody()) {
                responseBody = responseEntity.getBody();
            }
        }

        if (logRequestAnnotation.includeResponseBody()) {
            responseArguments.put(ARGUMENT_RESPONSE_BODY, responseBody);
        }

        return responseArguments;
    }

    private Map<String, Object> buildJsonArguments(ProceedingJoinPoint joinPoint,
                                                   MethodSignature signature,
                                                   LogRequestMethod logRequestAnnotation,
                                                   RequestMapping requestMappingAnnotation) {
        final Map<String, Object> jsonArguments = new LinkedHashMap<>();

        jsonArguments.put(ARGUMENT_PHASE, START);
        jsonArguments.put(ARGUMENT_METHOD, signature.getName());
        jsonArguments.put(ARGUMENT_ACTION, logRequestAnnotation.action());
        jsonArguments.put(ARGUMENT_REQUEST, buildRequest(joinPoint, signature,
                logRequestAnnotation, requestMappingAnnotation));
        jsonArguments.values().removeIf(o -> o == null || o.toString().trim().isEmpty());

        return jsonArguments;
    }

    private Map<String, Object> buildRequest(ProceedingJoinPoint joinPoint,
                                             MethodSignature signature,
                                             LogRequestMethod logRequestAnnotation,
                                             RequestMapping requestMappingAnnotation) {

        Map<String, Object> arguments = new HashMap<>();
        arguments.put(ARGUMENT_METHOD, getReqMappingHttpMethod(requestMappingAnnotation));
        arguments.put(ARGUMENT_PATH, getReqMappingPath(requestMappingAnnotation));
        arguments.putAll(buildRequestQueryAndPathParameters(joinPoint, signature, logRequestAnnotation));
        return arguments;
    }

    private Map<String, Object> buildRequestQueryAndPathParameters(ProceedingJoinPoint joinPoint,
                                                                   MethodSignature signature,
                                                                   LogRequestMethod logRequestAnnotation) {

        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] methodArgs = joinPoint.getArgs();

        Map<String, Object> arguments = new HashMap<>();
        Map<String, Object> pathParameterMap = new HashMap<>();
        Map<String, Object> queryParameterMap = new HashMap<>();
        Integer index = 0;

        for (Parameter parameter: parameters) {

            String key = parameter.getName();
            Object value = methodArgs[index++];

            PathVariable pathVar = parameter.getAnnotation(PathVariable.class);
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);

            if (pathVar != null) {
                pathParameterMap.put(
                        StringUtils.firstNonBlank(pathVar.name(), pathVar.value(), key), value);
            } else if (requestParam != null) {
                queryParameterMap.put(
                        StringUtils.firstNonBlank(requestParam.name(), requestParam.value(), key), value);
            } else if (requestBody != null && logRequestAnnotation.includeRequestBody()) {
                arguments.put(ARGUMENT_REQUEST_BODY, value);
            }
        }

        if (!pathParameterMap.isEmpty()) {
            arguments.put(ARGUMENT_PATH_PARAMETERS, pathParameterMap);
        }

        if (!queryParameterMap.isEmpty()) {
            arguments.put(ARGUMENT_QUERY_PARAMETERS, queryParameterMap);
        }

        return arguments;
    }


    private String getReqMappingPath(RequestMapping requestMappingAnnotation) {

        if (ArrayUtils.isNotEmpty(requestMappingAnnotation.path())) {
            return requestMappingAnnotation.path()[0];
        }

        if (ArrayUtils.isNotEmpty(requestMappingAnnotation.value())) {
            return requestMappingAnnotation.value()[0];
        }

        return StringUtils.EMPTY;
    }

    private String getReqMappingHttpMethod(RequestMapping requestMappingAnnotation) {

        if (ArrayUtils.isNotEmpty(requestMappingAnnotation.method())) {
            return requestMappingAnnotation.method()[0].name();
        }

        return StringUtils.EMPTY;

    }


}
