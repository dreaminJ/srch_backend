package com.miracle.srch.service;


import com.google.gson.*;
import com.miracle.srch.data.CountDao;
import com.miracle.srch.data.Keyword;
import com.miracle.srch.data.KeywordDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class APIService {
    @Autowired
    WebClient.Builder builder;

    @Value ( "${k.AK1}" )
    private String kakaoAK1;

    @Value ( "${k.AK2}" )
    private String kakaoAK2;

    @Value ( "${client.id}" )
    private String clientId;

    @Value ( "${client.secret}" )
    private String clientSecret;

    @Autowired
    KeywordDao keywordDao;

    @Autowired
    CountDao countDao;

    private WebClient webClient = WebClient.builder ( )
            .baseUrl ( "https://dapi.kakao.com" )
            .build ( );

    private WebClient nWebClient = WebClient.builder ( )
            .baseUrl ( "https://openapi.naver.com" )
            .build ( );

    public Mono<String> callApi ( String query, String url , int page, int size ) {
        Mono<String> res = this.webClient.get ( ).uri ( url, query, page, size )
                .header( "Authorization", kakaoAK1 )
                .accept ( MediaType.APPLICATION_JSON )
                .retrieve ( )
                .bodyToMono ( String.class );
        return res;
    }
    public Mono<String> callApi ( String query, String url ) {
        Mono<String> res = this.webClient.get ( ).uri ( url, query )
                .header( "Authorization", kakaoAK2 )
                .accept ( MediaType.APPLICATION_JSON )
                .retrieve ( )
                .bodyToMono ( String.class );
        return res;
    }
    public Mono<String> callNaverApi ( String query, String url ) {
        Mono<String> res = this.nWebClient.get ( ).uri ( url, query )
                .header ( "X-Naver-Client-Id", clientId )
                .header ( "X-Naver-Client-Secret", clientSecret )
                .accept ( MediaType.APPLICATION_JSON )
                .retrieve ( )
                .bodyToMono ( String.class );
        return res;
    }

    @Cacheable (cacheNames = "findPlaceCache")
    public JsonObject searchPlaceAPI ( String query, int page ,int size) {

        JsonObject result = new JsonObject ( );

        try {
            JsonObject JsonObject = getKakaoJsonObject ( query, page, size, result );
            searchImageAPI ( result, JsonObject );
        } catch (JsonSyntaxException e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            JsonObject JsonObject = getNaverJsonObject ( query, result );
            searchImageAPI ( result, JsonObject );
            return result;
        } catch (WebClientException e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            JsonObject JsonObject = getNaverJsonObject ( query, result );
            searchImageAPI ( result, JsonObject );
            return result;
        }catch (Exception e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            JsonObject JsonObject = getNaverJsonObject ( query, result );
            searchImageAPI ( result, JsonObject );
            return result;
        }

        return result;
    }

    public JsonObject getKakaoJsonObject ( String query, int page, int size, JsonObject result ) {
        String keyUrl = "/v2/local/search/keyword.json?query={query}&page={page}&size={size}";
        String apiResult = callApi ( query, keyUrl, page, size ).block ( );
        JsonObject JsonObject = JsonParser.parseString ( apiResult ).getAsJsonObject ( );
        int totalCount = JsonObject.get ( "meta" ).getAsJsonObject ( ).get ( "total_count" ).getAsInt ( );
        int pageSize = JsonObject.get ( "meta" ).getAsJsonObject ( ).get ( "pageable_count" ).getAsInt ( );

        JsonObject meta = new JsonObject ( );
        meta.addProperty ( "totalPages", totalCount/size );
        meta.addProperty ( "totalElements", totalCount );
        meta.addProperty ( "currentPage", page);
        meta.addProperty ( "pageSize", size );

        result.add ( "meta", meta );
        log.info ( "JsonObject 결과 값 :: " + JsonObject );
        return JsonObject;
    }

    public JsonObject getNaverJsonObject ( String query, JsonObject result ) {
        String keyUrl = "/v1/search/local.json?query={query}&display=10";
        String apiResult = callNaverApi ( query, keyUrl ).block ( );
        log.info ( "chk", apiResult );
        JsonObject JsonObject = JsonParser.parseString ( apiResult ).getAsJsonObject ( );
        int totalCount = JsonObject.get ( "total" ).getAsInt ( );
        // int pageSize=JsonObject.get( "meta" ).getAsJsonObject( ).get( "pageable_count" ).getAsInt( );

        JsonObject meta = new JsonObject ( );
        meta.addProperty ( "totalElements", totalCount );
        meta.addProperty ( "pageSize", 0 );
        meta.addProperty ( "totalPages", 0 );
        meta.addProperty ( "pageSize", 0 );
        result.add ( "meta", meta );
        log.info ( "JsonObject 결과 값 :: " + JsonObject );
        return JsonObject;
    }

    public void searchImageAPI ( JsonObject result, JsonObject JsonObject ) {
        try {
            String placeName = "";
            JsonElement jsonElement = JsonObject.get ( "items" );
            if(jsonElement==null){
                jsonElement = JsonObject.get ( "documents" );
            }
            JsonArray itemList = (JsonArray) jsonElement;
            JsonArray imgArray = new JsonArray ( );
            for ( int i = 0; i < itemList.size ( ); i++ ) {

                JsonObject places = new JsonObject ( );
                JsonObject place = (JsonObject) itemList.get ( i );
                if(place.get ( "place_name" ) != null ){
                    placeName = place.get ( "place_name" ).toString ( );
                }else {
                    placeName = place.get ( "title" ).toString ( );
                }

                searchImageKakao ( imgArray, places, placeName );
            }
            result.add ( "places", imgArray );
        } catch (JsonSyntaxException e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            setImageNaverAPI( result, JsonObject );
        } catch (WebClientException e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            setImageNaverAPI( result, JsonObject );
        } catch (NullPointerException e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            setImageNaverAPI( result, JsonObject );
        }catch (Exception e) {
            log.info ( "error naver api 호출" );
            e.printStackTrace ( );
            setImageNaverAPI( result, JsonObject );
        }
    }

    public void setImageNaverAPI ( JsonObject result, JsonObject JsonObject ) {
        String placeName = "";
        JsonElement jsonElement = JsonObject.get ( "items" );
        if(jsonElement==null){
            jsonElement = JsonObject.get ( "documents" );
        }
        JsonArray itemList = (JsonArray) jsonElement;
        JsonArray imgArray = new JsonArray ( );
        for ( int i = 0; i < itemList.size ( ); i++ ) {

            JsonObject places = new JsonObject ( );
            JsonObject place = (JsonObject) itemList.get ( i );
            if(place.get ( "place_name" ) != null ){
                placeName = place.get ( "place_name" ).toString ( );
            }else {
                placeName = place.get ( "title" ).toString ( );
            }

            searchImageNaverAPI ( imgArray, places, placeName );
            //반환 값 : imgArray, places,
        }
        result.add ( "places", imgArray );
    }

    public void searchImageKakao ( JsonArray imgArray, JsonObject places, String placeName ) {
        String callImgUrl = "/v2/search/image?query={query}";
        String imageResult = callApi ( placeName, callImgUrl ).block ( );

        places.addProperty ( "title", placeName );
        //String imageResult=imageUrls.get( 0 );

        JsonObject imgJsonObject = JsonParser.parseString ( imageResult ).getAsJsonObject ( );
        JsonElement imgJsonElement = imgJsonObject.get ( "documents" );
        log.debug ( "imgJson 결과 값 :: " + imgJsonElement );
        JsonArray imgItemList = (JsonArray) imgJsonElement;

        List<String> resultUrls = new ArrayList<String> ( );
        JsonArray urlArray = new JsonArray ( );
        for ( int j = 0; j < 3; j++ ) {
            if ( imgItemList.size ( ) > j ) {
                JsonObject img = (JsonObject) imgItemList.get ( j );
                String imgUrl = img.get ( "image_url" ).toString ( );
                resultUrls.add ( imgUrl );
                urlArray.add ( imgUrl );
            }
        }
        places.add ( "imageUrls", urlArray );
        imgArray.add ( places );
    }

    public void searchImageNaverAPI ( JsonArray imgArray, JsonObject places, String placeName ) {
            String callImgUrl = "/v1/search/image?query={query}";
            String imageResult = callNaverApi ( placeName, callImgUrl ).block ( );

            places.addProperty ( "title", placeName );
            //String imageResult=imageUrls.get( 0 );

            JsonObject imgJsonObject = JsonParser.parseString ( imageResult ).getAsJsonObject ( );

            JsonElement imgJsonElement = imgJsonObject.get ( "items" );

            log.debug ( "imgJson 결과 값 :: " + imgJsonElement );
            JsonArray imgItemList = (JsonArray) imgJsonElement;

            List<String> resultUrls = new ArrayList<String> ( );
            JsonArray urlArray = new JsonArray ( );
            for ( int j = 0; j < 3; j++ ) {
                if ( imgItemList.size ( ) > j ) {
                    JsonObject img = (JsonObject) imgItemList.get ( j );
                    String imgUrl = img.get ( "link" ).toString ( );
                    resultUrls.add ( imgUrl );
                    urlArray.add ( imgUrl );
                }
            }
            places.add ( "imageUrls", urlArray );
            imgArray.add ( places );
    }

    @Transactional
    public void saveKeyword ( String key ) {

        Optional<Keyword> keyword = keywordDao.findById ( key );

        keyword.ifPresent ( selectKeyword -> {
            selectKeyword.setCount ( keyword.get ( ).getCount ( ) + 1 );
            Keyword newKeyword = keywordDao.save ( selectKeyword );
        } );
        keywordDao.findById ( key ).orElseGet ( () -> keywordDao.save ( new Keyword ( key ) ) );

    }

    @Transactional ( readOnly = true )
    public List<Keyword> retrieveKeyword () {
        return countDao.findTop10ByOrderByCountDesc ( );
    }
}

