package com.familyFirstSoftware.SecureDocAIBackend.utils;

import com.familyFirstSoftware.SecureDocAIBackend.domain.Response;
import com.familyFirstSoftware.SecureDocAIBackend.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * Makes sure we return the same response for all requests
 */

public class RequestUtils {

    private static final BiConsumer<HttpServletResponse, Response> writeResponse = (httpServletResponse, response) -> {
        try{
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();

        } catch (Exception exception) {
            throw new ApiException(exception.getMessage());
        }
    };

    /*
        Order is important here

        TODO: extend more play around with this

     */

    private static final BiFunction<Exception, HttpStatus, String> errorReason = (exception, httpStatus) -> {
        if(httpStatus.isSameCodeAs(HttpStatus.FORBIDDEN)){
            return "You do not have permission for this resource.";
        }
        if(httpStatus.isSameCodeAs(HttpStatus.UNAUTHORIZED)){
            return "You are not logged in.";
        }
        if(exception instanceof AccessDeniedException || exception instanceof LockedException || exception instanceof BadCredentialsException
                || exception instanceof CredentialsExpiredException || exception instanceof ApiException){
            return exception.getMessage();
        }
        if(httpStatus.is5xxServerError()){
            return "Internal server error occurred.";
        }
        else {
            return "An error occurred. Please try again.";
        }
    };
    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(now().toString(), status.value(), request.getRequestURI(), HttpStatus.valueOf(status.name()), message, EMPTY, data);

    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception){
        if(exception instanceof AccessDeniedException){
            var apiResponse = getErrorResponse(request, response, exception, HttpStatus.FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }

    }

    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception, HttpStatus httpStatus) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        return new Response(now().toString(), httpStatus.value(), request.getRequestURI(), HttpStatus.valueOf(httpStatus.value()), errorReason.apply(exception, httpStatus), getRootCauseMessage(exception), emptyMap());
    }


}

