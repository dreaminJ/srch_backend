# 장소 검색 서비스

### 대용량 트래픽 
대용량 데이터의 반복적인 처리를 제어하기 위해 캐쉬로 저장. spring ehcache 적용 <br>
http://localhost:8080/actuator/caches 호출하여 저장된 캐쉬확인
- 개선 필요사항<br>
  selialize 기능이 정상작동하지 않음. 현재는 저장이 되는 데이터가 아니므로 영향이 없으나, 확장성을 위해서는 모델을 새로 정의 해야함

### 동시성 
검색어 저장에 대한 독립성을 보장하기 위해 트랜잭션 적용  

## Test case 
테스트 예상 결과는 프로젝트하위의 result 파일에 http명과 동일하게 추가하였음
1) 키워드를 통해 장소를 검색<br>
▶ 장소API에 있는 장소의 상호명(장소명,업체명)을 이용해 검색.<br>
▶ API에서 이미지 검색을 통해 연관된 이미지 3개를 추출하여 결과값에 포함
 (3개 이하면 그에 맞게 진행)<br>
▶ 검색결과의 Pagination 제공 <br>
　네이버 API의 경우는 최대 검색건수가 5이므로 페이징 적용 안함<br>
 　<B>(TEST)장소 검색<br></B>
   　1_checkSchPlcAPI.http 파일 수행<br>
　　<B>q : 키워드<br>
　　currentPage : 현재 페이지. default 1 <br>
　　pageSize : page 당 표시되는 결과 수. default 15</B><br>
 　결과 :1_chckSchrPlcAPI.txt <br>
▶ 카카오 API에 장애 시 네이버 API 호출<br>
 <B>　(TEST)카카오 API에 장애가 시 네이버 API를 통해 데이터가 제공 </B>- 서비스 재시작 필요<br>
 　. Naver 키워드 검색 호출 : 1_chckSchrPlcAPI.http 파일 수행 & application.properties의 k.AK1값 조작. 예를 들어 맨 끝에 한자리 삭제. >> 결과 파일 1_2_1_chckSchrPlcNvrAPI.txt<br>
 　. Naver 이미지 검색 호출 : 1_chckSchrPlcAPI.http 파일 수행 & application.properties의 k.AK2값 조작. 예를 들어 맨 끝에 한자리 삭제. >> 결과 파일 1_2_2_chckSchrPlcNvrImgAPI.txt
2) 인기 키워드 목록<br>
▶ 사용자들이 많이 검색한 순서대로, 최대 10개의 검색키워드를 제공<br>
▶ 1번에 대한 데이터가 저장되어 있으므로 프로그램 재시작<br>
 <B>　(TEST)검색 내용 조회</B> <br>
 　선작업 : 저장하기 위해 2_1_schrchKwrd.http 수행<br>
 　테스트 결과 확인을 위해 2_2_checkSvKwrd.http 수행
3) Exception Handling
▶ 서버 에러에 대한 처리<br>
▶ 에러 코드 정의<br>
 　EE01 : 검색 API 요청 오류. 요청 URL, 필수 요청 변수가 포함되지 않음.<br>
 　EE99 : 서버 내부 오류 <br>
 <B>　(TEST)검색어를 입력하지 않았을 경우 에러 발생</B><br>
 　3_1_excptonHndlng.http 수행<br>
 <B>　(TEST)서버 연결 장애 시 에러 발생 </B><br>
   application.properties의 k.AK1 / client.id 조작 >> 결과 파일 3_2_excptonSvrHndlng.txt
## endpoint 부하 개선 
endpoint에 대한 부하가 매우 커졌을 경우 확장할 수 있는 방법
WEB/WAS 서비스를 분리하고, WEB 서버에 로드밸런서를 두어 부하를 분산시킨다.
