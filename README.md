# PID microservice API project

## Documentation
The program has usage directory to add documentation, but currently it's still internal: 
https://wiki.eduuni.fi/display/CSCdatamanagementoffice/PID-service+REST+API+description
and 
https://wiki.eduuni.fi/display/CSCdatamanagementoffice/PID+Service+architechture
This project uses Quarkus 3.1.

It require database, which is plannet to run as service to many organizations: not so trivial as it can be.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn quarkus:dev
```
> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using `mvn package`.
It produces the `quarkus-run.jar` file in the `/target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
.mvn package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

I tried to create a native executable using: `mvn -Dquarkus.container-image.builder=jib package -Pnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel:20.2-java11`, but got  Image generation failed. Exit code: 125

Ja ylläoleva virhe korjautuu käyttämällä tunnusta, jonka uid on 1000 tai enemmän centos 8:ssa. Eli konento toimii.

You can create a native executable using: 
```shell script
mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/api-1.0.2-runner`


If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Authors

* **Pekka Järveläinen** 
