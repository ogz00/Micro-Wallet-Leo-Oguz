package com.oguz.demo.microwallet.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static com.oguz.demo.microwallet.exception.ErrorConstant.INTERNAL_SERVER_ERROR;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MicroWalletException extends RuntimeException {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private String details;
    private String message;
    @JsonIgnore
    private RuntimeException rootException;


    private MicroWalletException(Builder builder) {
        super(builder.message);
        this.message = builder.message;
        this.status = builder.status;
        this.timestamp = new Date();
        this.details = builder.details;
        this.rootException = builder.rootException;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public RuntimeException getRootException() {
        return rootException;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    public static class Builder {
        private HttpStatus status;
        private String details;
        private String message;
        private RuntimeException rootException;

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder setStatus(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder setDetails(String details) {
            this.details = details;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setRootException(RuntimeException rootException) {
            this.rootException = rootException;
            return this;
        }

        public MicroWalletException build() {
            return new MicroWalletException(this);
        }

        public MicroWalletException buildServerError() {
            return newInstance().setMessage(INTERNAL_SERVER_ERROR)
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

        public MicroWalletException buildWithStringFormatter(String entity, String... var) {
            return newInstance().setMessage(String.format(entity, var))
                    .setStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
