FROM clojure:lein-2.9.0

WORKDIR /api

ADD resources/ resources/
ADD src/ src/
ADD project.clj ./

RUN lein uberjar

EXPOSE 3000
