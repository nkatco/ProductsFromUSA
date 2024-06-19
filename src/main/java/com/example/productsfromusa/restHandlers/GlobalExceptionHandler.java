package com.example.productsfromusa.restHandlers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleSecurityException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> errorDetail = new HashMap<>();

        // Log the exception
        logger.error("An exception occurred:", exception);

        if (exception instanceof BadCredentialsException) {
            logger.error("Unauthorized access due to bad credentials.");
            errorDetail.put("status", 401);
            errorDetail.put("error", "Unauthorized");
            errorDetail.put("message", "The username or password is incorrect");
            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            logger.error("Access forbidden because the account is locked.");
            errorDetail.put("status", 403);
            errorDetail.put("error", "Forbidden");
            errorDetail.put("message", "The account is locked");
            return errorDetail;
        }

        if (exception instanceof AccessDeniedException) {
            logger.error("Access forbidden due to insufficient privileges.");
            errorDetail.put("status", 403);
            errorDetail.put("error", "Forbidden");
            errorDetail.put("message", "You are not authorized to access this resource");
            return errorDetail;
        }

        if (exception instanceof SignatureException) {
            logger.error("Access forbidden due to invalid JWT signature.");
            errorDetail.put("status", 403);
            errorDetail.put("error", "Forbidden");
            errorDetail.put("message", "The JWT signature is invalid");
            return errorDetail;
        }

        if (exception instanceof ExpiredJwtException) {
            logger.error("JWT token has expired.");

            // Clear cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }

            // Redirect to login page
            try {
                response.sendRedirect("/auth/login");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Default error response
        logger.error("Unknown internal server error.");
        errorDetail.put("status", 500);
        errorDetail.put("error", "Internal Server Error");
        errorDetail.put("message", "Unknown internal server error.");
        return errorDetail;
    }
}