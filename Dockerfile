# Official Tomcat image
FROM tomcat:8.5.39

# Copy the WAR file into the Tomcat webapps directory
COPY ./target/nrpro-backend.war /usr/local/tomcat/webapps/
COPY ./tomcat_conf/web.xml /usr/local/tomcat/conf/

# Expose port 8080 (Tomcat's default port)
EXPOSE 8080

# Specify the command to start Tomcat
CMD ["catalina.sh", "run"]
