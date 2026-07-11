package com.example.eventstream.inventory.handler;

import com.example.eventstream.inventory.exception.InsufficientInventoryException;
import com.example.eventstream.inventory.exception.InvalidInventoryOperationException;
import com.example.eventstream.inventory.exception.InventoryNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InventoryNotFoundException.class)
    public ProblemDetail handleInventoryNotFound(
            InventoryNotFoundException ex,
            HttpServletRequest request){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Inventory Not Found");
        problem.setDetail(ex.getMessage());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
    @ExceptionHandler(InsufficientInventoryException.class)
    public ProblemDetail handleInsufficientInventory(
            InsufficientInventoryException ex,
            HttpServletRequest request) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Insufficient Inventory");
        problem.setDetail(ex.getMessage());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(InvalidInventoryOperationException.class)
    public ProblemDetail handleInvalidOperation(
            InvalidInventoryOperationException ex,
            HttpServletRequest request) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Invalid Inventory Operation");
        problem.setDetail(ex.getMessage());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}
