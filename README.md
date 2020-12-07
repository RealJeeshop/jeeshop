Jeeshop ![CI/CD](https://github.com/remibantos/jeeshop/workflows/Build/badge.svg)
=======

# Description

Jeeshop e-commerce solution provides you with a store management GUI, [Jeeshop-Admin](#Jeeshop-Admin) and an complete set of RESTFul and [GraphQL](https://github.com/muskacirca/jeeshop-graph) APIs to be used by a store front-end application (typically a single page application):
* Products catalog
* Discounts
* Shopping cart
* Users management
* E-mailing

# Components
## <a name="Jeeshop-Admin">Jeeshop-Admin</a>
Jeeshop-Admin is a responsive single page application application designed to manage jeeshop stores.
It uses Jeeshop [RESTFul APIs](#backend) to perform common store management operations.

## <a name="backend">RESTFul APIs</a>
Jeeshop RESTFul APIs are designed to enable e-commerce and user management on a jeeshop store front-end application.
(They are also used heavily by [Jeeshop-Admin](#Jeeshop-Admin))

Jeeshop REST APIs are organized per domain:
* [Product catalogs](http://jeeshop.github.io/docs/1.0/rest/catalog/generated-docs/rest-api.html)
* [Users](http://jeeshop.github.io/docs/1.0/rest/user/generated-docs/rest-api.html)
* [Orders](http://jeeshop.github.io/docs/1.0/rest/order/generated-docs/rest-api.html)

## <a name="graphql">GraphQL APIs</a>
See [Jeeshop GraphQL](https://github.com/muskacirca/jeeshop-graphql) project

## <a name="backend">Jeestore</a>
Jeestore is a front-end demonstration application application which consumes Jeeshop [REST APIs](#backend).
You can take a look at it or start with it to build your e-commerce store.

# Development

Jeeshop use [quarkus](https://www.quarkus.io) as backend framework.

`application.properties file located in ./admin/src/main/resources enables to configure the app server.

to start app, run 
  ```shell script
    ./mvnw clean package quarkus:dev
  ```
# Installation

## with Docker
### Prerequisite
Create a specific jeeshop database and a jeeshop database applicative user (See section bellow for default database settings)

### Build

  ```shell script
    mvn package -Dquarkus.package.type=fast-jar
    docker build -t jeeshop/admin .
  ```
The first command build Jeeshop Admin with prod environment.
The second one build the docker image.

### Run 
To run jeeshop/admin image on port 8080 :
  ```shell script
    docker run -i --rm -p 8080:8080 -e PORT=8080 jeeshop/admin
  ```
If you want to run in debug mode, run the container using :
  ```shell script
    docker run -i --rm -p 8080:8080 -p 5005:5005 -e JAVA_ENABLE_DEBUG="true" -e PORT=8080 jeeshop/admin
  ```

The first command runs Jeeshop database migration scripts, which create Jeeshop tables and data.
The second one starts a Jeeshop instance with Jeeshop-Admin and Jeeshop RESTFul APIs exposed. It can be customised with the following environment variables:

### Configuration

| Environment variable  | Default | Description |
| -------------| ------------- | ------------- |
| JEESHOP_DATABASENAME | jeeshop  | |
| JEESHOP_DATABASE_HOSTNAME | host.docker.internal:5432 | host running this docker image |
| JEESHOP_DATABASE_USERNAME | jeeshop | ------------- |
| JEESHOP_DATABASE_PASSWORD | test  |------------- |
| JEESHOP_JDBC_DRIVER | postgresql  | postgresql, mysql |
| JEESHOP_CATALOG_DATABASENAME | jeeshop  |------------- |
| JEESHOP_CATALOG_DATABASE_HOSTNAME | host.docker.internal:5432  |------------- |
| JEESHOP_CATALOG_DATABASE_USERNAME | jeeshop | ------------- |
| JEESHOP_CATALOG_DATABASE_PASSWORD | test  |------------- |
| JEESHOP_CATALOG_JDBC_DRIVER | postgresql  | postgresql, mysql |
| JEESHOP_SSL_KEYSTORE_PATH | META-INF/resources/server.keystore  | default keystore used to provide ssl |
| JEESHOP_SSL_KEYSTORE_PASSWORD | test123 | default keystore password |
| JEESHOP_DATA_DIR | . | where jeeshop assets, such as catalog items images, should be uploaded. For docker build this value is set to /jeeshop |

## With docker-compose
  ```shell script
    docker-compose up
  ```

if binding JEESHOP_DATA_DIR to another directoty on local host, ensure you have the good permissions on the host (eg 1001:root)

# Demo dataset

All files needed for demo data re located in `install/src/main/resources/demo`
