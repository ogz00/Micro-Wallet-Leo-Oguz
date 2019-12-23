package com.oguz.demo.microwallet.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import static com.oguz.demo.microwallet.exception.ErrorConstant.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log4j2
public class MicroWalletResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private MicroWalletException getRoot(MicroWalletException ex) {
        if (ex.getRootException() != null && ex.getRootException() instanceof MicroWalletException)
            return getRoot((MicroWalletException) ex.getRootException());
        return ex;
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<MicroWalletException> handleAllExceptions(Exception ex, WebRequest request) {
        MicroWalletException microWalletException =
                MicroWalletException.Builder.newInstance()
                        .setDetails(request.getDescription(false))
                        .setMessage(ex.getMessage())
                        .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
        logger.error(ex);
        return new ResponseEntity<>(microWalletException, microWalletException.getStatus());
    }

    @ExceptionHandler(MicroWalletException.class)
    public final ResponseEntity<MicroWalletException> handleEntityNotFoundException(MicroWalletException ex, WebRequest request) {
        MicroWalletException root = getRoot(ex);
        logger.error(ex);
        return new ResponseEntity<>(root, root.getStatus());
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<MicroWalletException> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        MicroWalletException microWalletException =
                MicroWalletException.Builder.newInstance()
                        .setDetails(request.getDescription(false))
                        .setMessage(ex.getMessage())
                        .setStatus(HttpStatus.CONFLICT)
                        .build();
        logger.error(ex);
        return new ResponseEntity<>(microWalletException, microWalletException.getStatus());
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<MicroWalletException> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        MicroWalletException microWalletException =
                MicroWalletException.Builder.newInstance()
                        .setDetails(request.getDescription(false))
                        .setMessage(INVALID_SUPPLIED_ID)
                        .setStatus(HttpStatus.CONFLICT)
                        .build();
        logger.error(ex);
        return new ResponseEntity<>(microWalletException, microWalletException.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        MicroWalletException microWalletException =
                MicroWalletException.Builder.newInstance()
                        .setDetails(request.getDescription(false))
                        .setMessage(INVALID_JSON)
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .build();
        logger.error(ex);
        return new ResponseEntity<>(microWalletException, microWalletException.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        String detailedMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(ex.getMessage());
        MicroWalletException microWalletException =
                MicroWalletException.Builder.newInstance()
                        .setDetails(detailedMessage)
                        .setMessage(VALIDATION_FAILED)
                        .setStatus(HttpStatus.BAD_REQUEST)
                        .build();

        logger.error(ex);
        return new ResponseEntity<>(microWalletException, microWalletException.getStatus());
    }

}
