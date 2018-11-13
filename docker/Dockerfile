FROM clojure:lein-2.8.1 AS build

WORKDIR /src
COPY src /src/src
COPY resources /src/resources
COPY project.clj /src/
RUN lein deps
RUN apt-get update -y && apt-get install -y make
RUN lein uberjar

FROM trapperkeeper

COPY --from=build /src/target/pcp-broker-*-standalone.jar /src/

ENV CLIENT_AUTH "want"

COPY docker/conf.d/ /config/conf.d/
COPY docker/bootstrap.cfg /config/

COPY docker/Dockerfile /Dockerfile

