package com.miracle.srch.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class WebException extends Exception {

    /**
     * Exception Message을 기술해 주는 속성이다.
     */
    public String message;

    /**
     * 발생한 Exception의 Code을 나타내는 속성이다.
     */
    public String code;

    public WebException ( ) {
    }

    public WebException ( String var1, Throwable var2 ) {
        super ( var1, var2 );
    }

    public WebException ( Throwable var1 ) {
        super ( var1.getMessage ( ), var1 );
    }

    public Throwable getRootCause ( ) {
        Throwable var1;
        for ( var1 = this.getCause ( ); var1 != null && var1.getCause ( ) != null; var1 = var1.getCause ( ) ) {
            ;
        }

        return var1;
    }

    public String getStackTraceString ( ) {
        StringWriter var1 = new StringWriter ( );
        super.printStackTrace ( new PrintWriter ( var1 ) );
        return var1.toString ( );
    }

    public void printStackTrace ( PrintWriter var1 ) {
        var1.println ( this.getStackTraceString ( ) );
    }

    public WebException ( String var1 ) {
        super ( var1 );
    }

    /**
     * @param code    Exception의 Code을 나타내는 속성이다.
     * @param message Exception Message을 기술해 주는 속성이다.
     */
    public WebException ( String code, String message ) {
        super ( message );
        this.code = code;
        this.message = message;
    }

    /**
     * @param code    Exception의 Code을 나타내는 속성이다.
     * @param message Exception Message을 기술해 주는 속성이다.
     */
    public WebException ( String code, String message, Throwable throwable ) {
        super ( message, throwable );
        this.code = code;
        this.message = message;
    }

    /**
     * message Getter
     *
     * @return message
     */
    public String getMessage ( ) {
        return message;
    }

    /**
     * message Setter
     *
     * @param message Setting할 Message
     */
    public void setMessage ( String message ) {
        this.message = message;
    }

    /**
     * code Getter
     *
     * @return code
     */
    public String getCode ( ) {
        return code;
    }

    /**
     * code Setter
     *
     * @param code Setting할 code
     */
    public void setCode ( String code ) {
        this.code = code;
    }

}
