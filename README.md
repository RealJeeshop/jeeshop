Jeeshop [![Build Status](https://travis-ci.org/remibantos/jeeshop.svg?branch=master)](https://travis-ci.org/remibantos/jeeshop)
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

# Installation

## With Docker

### Prerequisite
1. Create a specific jeeshop database and a jeeshop database applicative user (See section bellow for default database settings)
2. Download Jeeshop database migration scripts available [here](https://github.com/remibantos/jeeshop/tree/master/install/src/main/resources/db/postgresql).

### <a name="docker-steps">Steps</a>
  ```shell script
    docker run --rm -v <Scripts Path>/postgresql:/flyway/sql flyway/flyway -url=jdbc:postgresql://host.docker.internal:5432/jeeshop -user=jeeshop -password=test migrate
    docker run -p 9990:9990 -p 8443:8443 -e JEESHOP_DATABASE_USERNAME=jeeshop -e jeeshop/jeeshop
  ```

The first command runs Jeeshop database migration scripts, which create Jeeshop tables and data.
The second one starts a Jeeshop instance with Jeeshop-Admin and Jeeshop RESTFul APIs exposed. It can be customised with the following environment variables:

| Environment variable  | Default | Description |
| -------------| ------------- | ------------- |
| JEESHOP_DATABASENAME | jeeshop  | |
| JEESHOP_DATABASE_HOSTNAME | host.docker.internal | host running this docker image |
| JEESHOP_DATABASE_USERNAME | jeeshop | ------------- |
| JEESHOP_DATABASE_PASSWORD | test  |------------- |
| JEESHOP_JDBC_DRIVER | postgresql  | postgresql, mysql |
| JEESHOP_CATALOG_DATABASENAME | jeeshop  |------------- |
| JEESHOP_CATALOG_DATABASE_HOSTNAME | host.docker.internal  |------------- |
| JEESHOP_CATALOG_DATABASE_USERNAME | jeeshop | ------------- |
| JEESHOP_CATALOG_DATABASE_PASSWORD | test  |------------- |
| JEESHOP_CATALOG_JDBC_DRIVER | postgresql  | postgresql, mysql |
| JEESHOP_SSL_KEYSTORE_PATH | /opt/jboss/wildfly/standalone/configuration/server.keystore  | default keystore used to provide ssl |
| JEESHOP_SSL_KEYSTORE_PASSWORD | test123 | default keystore password |
| JEESHOP_MEDIADIR | /tmp | where jeeshop assets, such as catalog items images, should be uploaded |



## On [Wildfy](http://wildfly.org/)
Jeeshop can be deployed to any Java EE 7 application server. (Web profile)

### <a name="wildfly-datasources">Datasources</a>
The following XA datasources are currently used by jeeshop modules and have to be created in server configuration
* JeeshopDS, for user and order data
* JeeshopCatalogDS for product catalog data

Below is an example of Jeeshop datasources configuration for a standalone server for Postgresql:

  ``` xml
  <xa-datasource jndi-name="java:/JeeshopDS" pool-name="JeeshopDS" enabled="true">
      <xa-datasource-property name="ServerName">
          localhost
      </xa-datasource-property>
      <xa-datasource-property name="DatabaseName">
          jeeshop
      </xa-datasource-property>
      <driver>postgresql</driver>
      <security>
          <user-name>jeeshop</user-name>
          <password>test</password>
      </security>
  </xa-datasource>
  <xa-datasource jndi-name="java:/JeeshopCatalogDS" pool-name="JeeshopCatalogDS" enabled="true">
      <xa-datasource-property name="ServerName">
          localhost
      </xa-datasource-property>
      <xa-datasource-property name="DatabaseName">
          jeeshop
      </xa-datasource-property>
      <driver>postgresql</driver>
      <security>
          <user-name>jeeshop</user-name>
          <password>test</password>
      </security>
  </xa-datasource>
  <drivers>
      <driver name="postgresql" module="org.postgresql">
          <driver-class>org.postgresql.Driver</driver-class>
          <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
      </driver>
  </drivers>
  ```

### <a name="wildfly-authentication">Security domain configuration</a>
A security domain named "jeeshop" has to be created to allow BASIC authentication and Role based access to protected REST Resources, using JaaS.

* Sample of configuration for a standalone server (tested with Postgresql):

``` xml
  <security-domain name="jeeshop" cache-type="default">
      <authentication>
          <login-module code="Database" flag="required">
              <module-option name="dsJndiName" value="java:/JeeshopDS"/>
              <module-option name="principalsQuery" value="select password from &quot;user&quot; where login = ? and (disabled is null or disabled = false) and activated = true"/>
              <module-option name="rolesQuery" value="select name,'Roles' from &quot;role&quot; r, user_role ur, &quot;user&quot; u where u.login=? and u.id = ur.userId and r.id = ur.roleId"/>
              <module-option name="hashAlgorithm" value="SHA-256"/>
              <module-option name="hashEncoding" value="base64"/>
              <module-option name="unauthenticatedIdentity" value="guest"/>
          </login-module>
      </authentication>
  </security-domain>
  ```

#### Configure SSL to secure channels

SSL has to be configured in order to secure credentials sent in requests performed by store customers or Jeeshop-admin users (ie store administrators).
This action is not required under a PaaS such as Openshift. (See [Openshift deployment](#openshift))

* Create server certificate

Execute the following command in a temp directory

    keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -validity 7360 -keystore server.keystore -keypass <PASSWORD FOR PRIVATE KEY>  -storepass <PASSWORD FOR KEYSTORE> -dname "cn=Server Administrator,o=jeeshop,c=FR"

Copy the server.keystore file in to the ${jboss.home.dir}/standalone/configuration folder

* In standalone.xml configuration file

Add the following security realm block :

``` xml
    <security-realms>
        ...
        <security-realm name="SSLRealm">
            <server-identities>
                <ssl>
                    <keystore path="server.keystore" relative-to="jboss.server.config.dir" keystore-password="THE KEYSTORE PASSWORD"/>
                </ssl>
            </server-identities>
        </security-realm>
    </security-realms>
  ```

Add the following http-listener line to the server block (in undertow subsystem)

``` xml
    <server name="default-server">
        ...
       <https-listener name="default-https" socket-binding="https" security-realm="SSLRealm"/>
       ...
     </server>
  ```

### Jeeshop JBOSS Module
A JBOSS Module named "jeeshop" have to be created to <WILDFLY HOME>/modules directory.
It contains multiple configuration properties such as:
* Mailer parameters (SMTP parameters, Sender...)
Sample of this module configuration is available in .openshift directory

### Jeeshop media directory

To serve Jeeshop catalog media files uploaded through Jeeshop-Admin, the following configurations have to be added in standalone.xml configuration file:

Add the following in undertow subsystem

``` xml
<subsystem xmlns="urn:jboss:domain:undertow...">
            ...
            <server name="default-server">
               ...
                <host name="default-host" alias="localhost">
                   ...
                    <location name="/jeeshop-media" handler="jeeshop-media"/>
                   ...
                </host>
            </server>
            ...
            <handlers>
                ...
                <file name="jeeshop-media" path="/home/remi/jeeshop-media" directory-listing="true"/>
                ...
            </handlers>
            ...
        </subsystem>
  ```

## Database setup
### Scripts
Database setup scripts are provided in ./install/src/main/db directory for Mysql and Postgresql databases.

* Vx.x_1__jeeshop-install.sql creates jeeshop ddl and reference data. It creates also a single user with login/password admin@jeeshop.org/jeeshop (password is hashed using SHA-256 in this script, which must match security domain configuration, see [Security domain configuration](#wildfly-authentication)). This user should be deleted in production environment for security reason.
* Vx.x__jeeshop-drop.sql empties database
* Vx.x_2__demo-catalog-data configures jeeshop demonstration catalog data

### Applying with maven

To setup or update your Jeeshop db with latest SQL scripts version, you can check a "flyway" maven profile, such as flyway-mysql, and execute "install" maven goal like this:
```
    mvn install
```

This command applies Jeeshop SQL scripts using [*FLYWAY*](https://flywaydb.org) database migration tool.
So you always get your Jeeshop database up-to date applying SQL scripts this way.

Notes:
Current database scripts are made for a single database.
But you can also use one database for product catalog data and another for user and order data. (See [Datasources](#wildfly-datasources) section above)
