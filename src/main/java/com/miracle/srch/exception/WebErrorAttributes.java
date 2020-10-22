package com.miracle.srch.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
@Getter
@Slf4j
@NoArgsConstructor ( access = AccessLevel.PUBLIC )
public class WebErrorAttributes extends DefaultErrorAttributes {

    private String message;
    private String code;

    public WebErrorAttributes (  String code , String message ) {
        this ( );
        this.code = code;
        this.message = message;
    }
}
