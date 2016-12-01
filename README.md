# STREETLIFE MMECP Control Panel

Implementation of STREETLIFE Mobility and Emission Control Panel Backend in Java.


# Installation

**Prerequisite**
Oracle/OpenJDK JDK >= 1.8
Apache Maven >= 3.2
JBoss WildFly (Java EE7 Full & Web Distribution) >= 8.1
GIT >= 1.9.4

**Setup your system**
Start your installed WildFly and go to ​http://localhost:9990/console. Follow the instructions on that site to create a management user for WildFly. If you want to access WildFly from another machine like localhost, you have to configurate the interfaces: ​https://docs.jboss.org/author/display/WFLY8/Interfaces+and+ports

Create or edit your Maven settings in ~/.m2/settings.xml. Insert the following XML:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

<localRepository>${user.home}/.m2/repository</localRepository>
<interactiveMode>true</interactiveMode>
<usePluginRegistry>false</usePluginRegistry>
<offline>false</offline>

<profiles>
   <profile>
       <id>streetlife</id>
       <properties>
         <wildfly-hostname>localhost</wildfly-hostname>
         <wildfly-port>9990</wildfly-port>
         <wildfly-username>YOUR_WILDFLY_USERNAME</wildfly-username>
         <wildfly-password>YOUR_WILDFLY_USERNAME_PASSWORD</wildfly-password>
         <storage.url.base>http://private-f5ae7-streetlifemmecp.apiary-mock.com</storage.url.base>
         <mmecp.backend.api.websocket.url>ws://localhost:8080/api-websocket/</mmecp.backend.api.websocket.url>
         <generic.data.api.url>http://private-f559f-streetlifehistoricdataapi.apiary-mock.com</generic.data.api.url>
         <live.data.api.url>http://private-91266-streetlifelivedataapi.apiary-mock.com</live.data.api.url>
         <cip.api.url>https://www.cityintelligenceplatform.siemens.com/cip-mobility/rest/streetlife/parkride</cip.api.url>
         <berlin.bike.url>http://private-caca6-mmecpapi1.apiary-mock.com/cip-mobilityguidance/rest/mobilityguidance/bike</berlin.bike.url>
         <berlin.event.url>http://private-caca6-mmecpapi1.apiary-mock.com/cip-mobilityguidance/rest/mobilityguidance/event</berlin.event.url>
         <berlin.profile.url>http://private-caca6-mmecpapi1.apiary-mock.com/cip-mobilityguidance/rest/mobilityguidance/profile</berlin.profile.url>
         <berlin.userservice.url>http://private-anon-56cabc756-streetlifeuserservicev2.apiary-mock.com</berlin.userservice.url>
         <cip.username>...</cip.username>
         <cip.password>...</cip.password>
       </properties>
    </profile>
</profiles>

<activeProfiles>
   <activeProfile>streetlife</activeProfile> <!-- This ensures that maven is using the streetlife profile with each run -->
</activeProfiles>

</settings>
```

Please visit [https://gitlab.fokus.fraunhofer.de/streetlife/mmecp/wikis/home](https://gitlab.fokus.fraunhofer.de/streetlife/mmecp/wikis/home) to fetch the cip username and password.
Or get in Contact if you are not member of the project team. Load the mmecp maven project to your IDE (e.g. IntelliJ, Eclipse, Netbeans).

**Get it running**
Start your WildFly server.

Build all necessary components. Run each of the following Maven commands from the MMECP root directory:
```mvn
mvn clean package # This removes potential artefacts from former builds and packages all components
mvn wildfly:deploy-only # this will deploy the mmecp websocket api
```

**See it running**
Open your preferred web browser and go to:
[http://localhost:8080/api-rest/subscription/channel/some_channel_id/notification](http://localhost:8080/api-rest/subscription/channel/some_channel_id/notification)  for an example call to the mmecp subscription API

[http://localhost:8080/panelUI](http://localhost:8080/panelUI) 
for the Dashboard

# Contact
Feel free to contact us if you want to contribute or in case of any questions.
benjamin.dittwald@fokus.fraunhofer.de