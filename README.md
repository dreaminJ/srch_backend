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
 　(TEST1)장소 검색<br>
   　1_checkSchPlcAPI.http 파일 수행<br>
　　<B>q : 키워드<br>
　　currentPage : 현재 페이지. default 1 <br>
　　pageSize : page 당 표시되는 결과 수. default 15</B><br>
결과 : <br>


 　(TEST2)카카오 API에 장애가 발생한 경우,해당 결과값을 낼 수 있는 네이버 API를 통해 데이터가 제공 - 서비스 재시작 필요<br>
   1_chckSchrPlcAPI.http 파일 수행 & application.properties의 k.AK1값 조작. 예를 들어 맨 끝에 한자리 삭제. >> 결과 파일 1_2_1_chckSchrPlcNvrAPI.txt<br>
   1_chckSchrPlcAPI.http 파일 수행 & application.properties의 k.AK2값 조작. 예를 들어 맨 끝에 한자리 삭제. >> 결과 파일 1_2_2_chckSchrPlcNvrImgAPI.txt
2) 인기 키워드 목록<br>
▶ 사용자들이 많이 검색한 순서대로, 최대 10개의 검색키워드를 제공<br>
▶ 1번에 대한 데이터가 저장되어 있으므로 프로그램 재시작<br>
▶ 검색 내용을 저장하기 위해 2_1_schrchKwrd.http 수행<br>
▶ 테스트 결과 확인을 위해 2_2_checkSvKwrd.http 수행
3) Exception Handling
▶ 서버 에러에 대한 처리<br>
▶ 검색어를 입력하지 않았을 경우 에러 발생 3_excptonHndlng.http 수행
## endpoint 부하 개선 
endpoint에 대한 부하가 매우 커졌을 경우 확장할 수 있는 방법
WEB/WAS 서비스를 분리하고, WEB 서버에 로드밸런서를 두어 부하를 분산시킨다.
