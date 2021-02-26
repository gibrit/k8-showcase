FROM fabric8/java-alpine-openjdk8-jre
ENV VERTICLE_FILE front-app-microservice-jar-with-dependencies.jar
ENV VERTICLE_HOME /usr/verticles
COPY  ./webroot   $VERTICLE_HOME/webroot
COPY ./target/$VERTICLE_FILE $VERTICLE_HOME/
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
#ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5000"
CMD ["exec java $JAVA_OPTS  -jar $VERTICLE_FILE"]

