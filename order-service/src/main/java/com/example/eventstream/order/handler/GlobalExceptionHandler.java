package com.example.eventstream.order.handler;

import com.example.eventstream.order.exception.OrderNotFoundException;
import com.example.eventstream.order.exception.PricingServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(
            OrderNotFoundException ex,
            HttpServletRequest request) {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Order Not Found");
        problem.setDetail(ex.getMessage());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }

    @ExceptionHandler(PricingServiceUnavailableException.class)
    public ProblemDetail handlePricingServiceUnavailable(
            PricingServiceUnavailableException ex,
            HttpServletRequest request) {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);

        problem.setTitle("Pricing Service Unavailable");
        problem.setDetail(ex.getMessage());
        problem.setProperty("path", request.getRequestURI());

        return problem;
    }
}