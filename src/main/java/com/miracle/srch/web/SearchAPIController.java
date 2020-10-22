package com.miracle.srch.web;

import com.google.gson.JsonObject;
import com.miracle.srch.data.Keyword;
import com.miracle.srch.exception.WebErrorAttributes;
import com.miracle.srch.service.APIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
public class SearchAPIController {


    @Autowired
    APIService service;

    @GetMapping ( value = "v1/place" )
    public ResponseEntity retrievePlace ( ModelMap model
            , @RequestParam (value="q" )String query
            , @RequestParam (required = false, value="currentPage", defaultValue ="1" ) int currentPage
            , @RequestParam(required = false, value="pageSize", defaultValue ="15") int pageSize) {
        JsonObject result=new JsonObject( );
        try {
            result = service.searchPlaceAPI( query, currentPage , pageSize);
            service.saveKeyword( query );
        } catch (Exception e) {
            log.error("error occurs",e);
            WebErrorAttributes errorAttributes = new WebErrorAttributes ( "EE99" ,"server error. 관리자에게 문의해 주시기 바랍니다.");
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorAttributes );
        }
        return ResponseEntity.status( HttpStatus.OK ).body( result );
    }

    @GetMapping ( value = "v1/place/keyword" )
    public ResponseEntity retrieveKeyword(){

        try {
            List<Keyword> result =  service.retrieveKeyword(  );
            return ResponseEntity.status( HttpStatus.OK ).body( result );
        } catch (Exception e) {
            log.error("error occurs",e);
            WebErrorAttributes errorAttributes = new WebErrorAttributes ( "EE99" ,"server error. 관리자에게 문의해 주시기 바랍니다.");
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorAttributes );
        }
    }
    @GetMapping ( value = "/error" )
    public ResponseEntity hadleError(){
        log.error("error occurs");
        WebErrorAttributes errorAttributes = new WebErrorAttributes ( "EE99" ,"server error. 관리자에게 문의해 주시기 바랍니다.");
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorAttributes );

    }

}
