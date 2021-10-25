FROM maven:3.6-openjdk-8 AS MAVEN_TOOL_CHAIN

COPY . /tmp

WORKDIR /tmp

RUN mvn package -DskipTests=true

FROM openjdk:8-jre-alpine
RUN apk update && apk --no-cache add vim procps binutils curl bash wget openssl
ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-alpine-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-alpine-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-alpine-linux-amd64-$DOCKERIZE_VERSION.tar.gz



COPY conf /conf/

RUN ls -la /conf/*

COPY startup.sh /
RUN chmod +x /startup.sh
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/AppD-Prometheus-Extension-1.0.0.jar /

ENTRYPOINT [ "/startup.sh" ]