jeeshop
=======
Work In Progress

# Description
TODO
# Modules
TODO
# Deployment
Jeeshop components can be deployed to any Java EE 7 compliant server.
## Wildfly 8 configurations
### Datasource
The following XA datasources are currently used by jeeshop modules and have to be created in server configuration
* UserDS
* CatalogDS


Sample of configurations for a standalone server and a MySQL database:
  ```xml
  <xa-datasource jndi-name="java:/CatalogDS" pool-name="CatalogDS" enabled="true">
      <xa-datasource-property name="ServerName">
          localhost
      </xa-datasource-property>
      <xa-datasource-property name="DatabaseName">
          catalog
      </xa-datasource-property>
      <driver>mysql</driver>
      <security>
          <user-name>testapp</user-name>
          <password>testapp</password>
      </security>
      <validation>
          <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"/>
          <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"/>
      </validation>
  </xa-datasource>
  <xa-datasource jndi-name="java:/UserDS" pool-name="UserDS" enabled="true">
      <xa-datasource-property name="ServerName">
          localhost
      </xa-datasource-property>
      <xa-datasource-property name="DatabaseName">
          user
      </xa-datasource-property>
      <driver>mysql</driver>
      <security>
          <user-name>testapp</user-name>
          <password>testapp</password>
      </security>
      <validation>
          <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"/>
          <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"/>
      </validation>
  </xa-datasource>
  <driver name="mysql" module="com.mysql">
      <driver-class>com.mysql.jdbc.Driver</driver-class>
      <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
  </driver>
  ```
  

### Security domain configuration
A security domain named "jeeshop" has to be created. 
Sample of configuration for a standalone server: (TODO chose encryption mechanism / algo)
  ```xml
  <security-domain name="jeeshop">
      <authentication>
          <login-module code="Database" flag="required">
              <module-option name="dsJndiName" value="java:/UserDS"/>
              <module-option name="principalsQuery" value="select password from User where login = ?"/>
              <module-option name="rolesQuery" value="select role,'Roles' from Role r, User_Role ur, User u where u.login=? and u.id = ur.userId and r.id = ur.roleId"/>
              <module-option name="hashAlgorithm" value="MD5"/>
              <module-option name="hashEncoding" value="base64"/>
              <module-option name="unauthenticatedIdentity" value="guest"/>
          </login-module>
      </authentication>
  </security-domain>
  ```
  
