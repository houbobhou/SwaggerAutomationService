FROM websphere-liberty:webProfile7
EXPOSE 9080
ADD ${appLocation}/target/SwaggerParser-0.0.1-SNAPSHOT.war /config/dropins/
COPY server.xml /config/server.xml
