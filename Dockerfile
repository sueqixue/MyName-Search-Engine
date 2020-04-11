FROM openkbs/jdk-mvn-py3-x11
WORKDIR /Users/xueqi/desktop/1660Project/myNameSearch
COPY myNameSearch.jar myNameSearch.jar
COPY cs1660.json cs1660.json
COPY Data Data
ENV GOOGLE_APPLICATION_CREDENTIALS /Users/xueqi/desktop/1660Project/myNameSearch/cs1660.json
CMD java -jar myNameSearch.jar