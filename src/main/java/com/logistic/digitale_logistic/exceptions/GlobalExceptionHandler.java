package com.logistic.digitale_logistic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbeidenException.class)
    public ProblemDetail handleForbeidenException(ForbeidenException ex){
      ProblemDetail  pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
      pd.setDetail(ex.getMessage());
      pd.setTitle("Forbidden");
        return pd;
    }
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ProblemDetail handleInvalidRefreshTokenException(InvalidRefreshTokenException ex){
        ProblemDetail pd  = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("UNAUTHORIZED");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
