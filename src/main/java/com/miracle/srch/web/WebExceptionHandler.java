package com.miracle.srch.web;

import com.miracle.srch.exception.WebErrorAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {


    @ExceptionHandler ( NoHandlerFoundException.class )
    public ResponseEntity handleNoHandlerFoundException ( NoHandlerFoundException e ) {
        e.printStackTrace ( );
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "server error. 관리자에게 문의해 주시기 바랍니다." );
    }
    // 필수 파라미터 정의안되었을 경우
    @ExceptionHandler ( MissingServletRequestParameterException.class )
    public ResponseEntity handleMissingServletRequestParameterException ( MissingServletRequestParameterException e ) {

        WebErrorAttributes errorAttributes = new WebErrorAttributes ( "EE01" , e.getParameterName ()+" 잘못된 쿼리 요청입니다. 관리자에게 문의해 주시기 바랍니다.");
        return new ResponseEntity( errorAttributes, HttpStatus.BAD_REQUEST );
    }
}
