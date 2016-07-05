FROM websphere-liberty:webProfile7
EXPOSE 9080
ADD ${appLocation}/target/SwaggerParser.war /config/dropins/

