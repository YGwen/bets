# FredBet

## What´s FredBet?
Original Repo is : https://github.com/fred4jupiter/fredbet

Simple football betting application using [Spring Boot](https://projects.spring.io/spring-boot/), [Thymeleaf](http://www.thymeleaf.org/) and [Bootstrap](http://getbootstrap.com/). The web pages are constructed in responsive design for using on mobile devices.

## Features

- simple betting of football championchips
- responsive design (mobile first design)
- extra betting of 1st, 2nd and 3rd winner
- image gallery (image storage support: filesystem, database, AWS S3)
- user profile image
- multiple database types supported (H2, MariaDB, MySQL, PostgeSQL)
- integrated user administration
- rich text editor for rules, prices and misc pages
- points statistic
- display other users bets after match kickoff
- ranking page
- Microsoft Excel match import (ready to use world championchip 2018 template available)
- Microsoft Excel bets, statistic export
- language switcher (supported languages: englisch, german (by now))
- ranking filter for adults and childs listing
- integrated testing capabilities: create demo users, matches, bets...
- configurable runtime configuration

## Testing it locally

You can run the application by issuing the following command:

```bash
mvn install spring-boot:run
```

The application is available under [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running the released Docker image

The released docker image is available on [Docker Hub](https://hub.docker.com/r/fred4jupiter/fredbet).

```bash
docker run -d -p 8080:8080 fred4jupiter/fredbet
```

## Building your own Docker image

```bash
mvn clean install
docker build -t fred4jupiter/fredbet .
docker run -d -p 8080:8080 fred4jupiter/fredbet
```

This will build (and run) an image with name `fred4jupiter/fredbet`.

## Running with Docker Compose

There are some docker compose files available to run the application e.g. with a separate database. This configuration is recommended for production use.

You can find the docker compose files in folder

```
src/docker/docker-compose
```

Example for FredBet with MariaDB:

```bash
cd src/docker/docker-compose
docker-compose -f mariadb.yml up -d
```

## FredBet Properties

These properties has to be set at application startup.

| Key | Default Value | Description |
|--------|--------|--------|
| spring.profiles.active | dev | Active Spring profile at startup. Possible values: `dev,prod,localdb`. Use profile `prod` for real productive setup. |
| fredbet.image-size | 1920 | Pixel length side for storing images in photo gallery. |
| fredbet.thumbnail-size | 75 | Pixel length side for storing thumbnail images. |
| fredbet.image-location | FILE_SYSTEM | Location where to store the images/photos. Possible values: `FILE_SYSTEM, DATABASE, AWS_S3` |
| fredbet.image-file-system-base-folder | the users home folder | In case you selected to save the images in file system this is the path to the folder. |
| fredbet.aws-s3bucket-name | fredbet | Name of the AWS bucket if the image location is set to AWS_S3. |

Please have a look at [Spring Boots externalized configuration documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) on how to setup these properties as JVM parameters or environment variables.

## Database Properties

| Key | Default Value | Description |
|--------|--------|--------|
| spring.datasource.hikari.jdbc-url | | The database jdbc connection url, e.g. `jdbc:mariadb://localhost:3306/fredbetdb`. |
| spring.datasource.hikari.username | | The database username. |
| spring.datasource.hikari.password | | The database password. |
| spring.datasource.hikari.driver-class-name |  | One of: `org.h2.Driver, com.mysql.jdbc.Driver, org.mariadb.jdbc.Driver, org.postgresql.Driver` |

## Runtime Configuration

Some configuration parameters are configurable at runtime. You has to be administrator to edit these values. Following settings can be changed at runtime:

- Log Level
- Clearing caches (for available navigation entries, user child relation on ranking page, runtime configuration parameters)
- enable separate adults and children ranking
- changeable usernames
- menu entry for generating test data (for testing only!)
- favourite country (will be used in points statistics)
- used password on password reset
- points for extra bets

## Production Environment

FredBet is designed to run within the Amazon Web Services (AWS) cloud as production environment. Typically you run the docker container in EC2 container service (ECS) with these environment properties while storing the images of the image gallery in S3:

| Key | Value | Description |
|--------|--------|--------|
| spring.profiles.active | prod |  |
| fredbet.image-location | AWS_S3 |  |
| fredbet.aws-s3bucket-name | fredbet | Or any other name for your S3 bucket. |

Add also the properties for your database connection (see above).

Be sure to use an instance profile with sufficient privileges for S3. You can ajust these values with the following properties:

| Key | Default Value | Description |
|--------|--------|--------|
| cloud.aws.credentials.profileName |  ecsInstanceRole | name of the instance profile |

The policy to access your S3 bucket will look like this:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "s3:*",
            "Resource": "arn:aws:s3:::<BUCKET_NAME>/*"
        }
    ]
}
```

If you not want to use the instance profile for authorization (or you can´t, e.g. not running in AWS) you can set the access key and secret access key manually with these environment variables:

| Key | Value |
|--------|--------|
| cloud.aws.credentials.accessKey |  XXX |
| cloud.aws.credentials.secretKey |  XXX |
| cloud.aws.credentials.instanceProfile |  false |

## Hints

```bash
-Dliquibase.enabled = false
```
Disabling Liquibase database migration at all. This may be useful if you have an already populated database schema.

## Health Check

You can call this URL for a health check:

[http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

You will see a response of `{"status":"UP"}`. The health check URL is callable without authentication.

## Screenshot

![FredBet Screenshot](src/docs/screenshot/Screenshot1.jpg?raw=true "FredBet Screenshot")

## License

<a rel="license" href="http://creativecommons.org/licenses/by-sa/4.0/">
	<img alt="Creative Commons Lizenzvertrag" style="border-width:0" src="https://i.creativecommons.org/l/by-sa/4.0/88x31.png" />
</a> FredBet (c) by Michael Stähler

FredBet is licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.

You should have received a copy of the license along with this work. If not, see [http://creativecommons.org/licenses/by-sa/4.0/](http://creativecommons.org/licenses/by-sa/4.0/).

## Travis Build Status
[![Build Status](https://travis-ci.org/fred4jupiter/fredbet.svg?branch=master)](https://travis-ci.org/fred4jupiter/fredbet)
