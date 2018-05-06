# Courmessenger

A encrypted messenger for Coursera Capstone project of Cybersecurity specialization

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You will need the following packages

```
 Java 8
 Maven 3
```

So create an __application-dev.properties__ based on the example

```
cp src/main/resources/application-dev-example.properties src/main/resources/application-dev.properties 
```


## Running in development environment (Maven)


Run the project with maven. Not we specified the __dev__ profile.

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Finally access in the url

```
http://localhost:5813
```

## Running in development environment (Eclipse)


Firstly, import the project in eclipse.

So configure to run the class

```
org.coursera.cybersecurity.courmessenger.Application
```

and set the property in "VM Argument"

```
-Dspring.profiles.active=dev
```

Finally access in the url

```
http://localhost:5813
```

## Running in production environment (Heroku)

Create the host

```
heroku create
```

Push the code

```
git push heroku master
```

Create a random APP_MASTER_SECRET

```
heroku config:set APP_MASTER_SECRET=$(dd if=/dev/urandom of=/dev/stdout bs=1 count=32 status=none | base64)
```
