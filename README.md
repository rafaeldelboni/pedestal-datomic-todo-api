# pedestal-datomic-todo-api
[![Status][badge-status]][badge-status]
#### Mixing some Clojure, Pedestal and Datomic to create an API

## Built With

* [Clojure](https://clojure.org/)
* [Leiningen](https://leiningen.org/)
* [Pedestal](https://github.com/pedestal/pedestal)
* [Datomic](https://www.datomic.com)
* [Docker](https://docs.docker.com/)

## Getting Started

These instructions will get the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install to run this project

* [Java](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html)
* [Clojure](https://clojure.org/guides/getting_started)
* [Leiningen](https://leiningen.org/)
* [DatomicFree](https://my.datomic.com/downloads/free)

### Restore dependencies
```
lein deps
```

### Run the application

```
lein run
```
This command should start a server on [http://localhost:8080](http://localhost:8080).

## Running the tests

To run all the tests
```
lein test
```

## Docker

You can use Docker to setup your local dev environment database or test the api without need any above installed.

### Prerequisites

* [Docker](https://docs.docker.com/install/)

### Datomic Only

    $ docker-compose -f docker-compose-db.yml build
    $ docker-compose -f docker-compose-db.yml up -d

### Api and Datomic

    $ docker-compose -f docker-compose-dev.yml build
    $ docker-compose -f docker-compose-dev.yml up -d
This command should start a server on [http://localhost:3000](http://localhost:3000).

[badge-status]: https://img.shields.io/badge/status-work%20in%20progress-lightgrey.svg
