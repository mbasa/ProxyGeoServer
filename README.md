# ProxyGeoServer
Simple Proxy Service for a Geoserver installation which can limit the WMS Requests. 
This Proxy Service removes the need to directly expose GeoServer and its Dashboard outside of the Firewall.

### Build
To create a WAR file for Tomcat deployment using Maven:

```
mvn clean install
```
### Installation
Edit the properties file after Tomcat deployment and set the GeoServer location and the Allowed Requests for WFS and WMS services.

### Access 
* For WMS

```
http://localhost:8080/ProxyGS/pwms?<WMS Request Parameters>
```

* For WFS

```
http://localhost:8080/ProxyGS/pwfs?<WFS Request Parameters>
```

### License
GPLv3.0

