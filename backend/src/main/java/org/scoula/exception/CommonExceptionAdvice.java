package org.scoula.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
@Order(1)
public class CommonExceptionAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex) {
        return "/resources/index.html";
    }

//    @ExceptionHandler(Exception.class)
//    public String except(Exception ex, Model model) {
//        log.error("Exception......" + ex.getMessage());
//        model.addAttribute("exception", ex);
//
//        return "error_page";
//    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public String handle404(NoHandlerFoundException ex, HttpServletRequest request, Model model) {
//        log.error("404 예외 발생 -> ", ex);
//        model.addAttribute("uri", request.getRequestURI());
//
//        return "custom404";
//    }
}
