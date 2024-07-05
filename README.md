# Confluent Data Contract

## Pre-requisites

Have the following tools installed:
- Docker
- Docker Compose
- Maven
- Java 17
- jq

## Start the environment and compile the code

```shell
    cd env
    docker-compose up -d
    cd ..
    # compile the project and move to the app folder
    mvn clean package
```

Control center is available under http://localhost:9021

## Register  schemas

### Register the strong type `clientId` with a validation rule

```shell
   jq -n \
     --rawfile schema src/main/resources/schemas/clientId.avsc \
     --slurpfile rules src/main/resources/schemas/clientId-ruleset.json \
     '{"schemaType": "AVRO", schema: $schema, ruleSet: $rules[0]}' | \
  curl --silent http://localhost:8081/subjects/clientId/versions --json  @- | jq
```
> [!NOTE]
> * For simplicity, we use `jq` to read the files and send the request to the schema registry.
> * The schema `clientId` is registered with a validation rule.

### Register the schema `client` using the strong type `clientId`

```shell
  jq -n --rawfile schema src/main/resources/schemas/client.avsc '{schema: $schema, 
  references: [
    {
      "name": "pro.tomasalmeida",
      "subject": "clientId",
      "version": 1
    }
  ]}' | \
  curl --silent http://localhost:8081/subjects/client-value/versions --json @- | jq
```
> [!NOTE]
> * We need to give the subject, version and name of the schema `clientId` to the schema `client`.
> * We need to fix a version for the schema `clientId` to be used by the schema `client`.


## Compile and test the code

```shell
    mvn clean package
    java -jar target/schema-strong-type-1.0-SNAPSHOT-jar-with-dependencies.jar
```

> [!CAUTION]
> * Rules are not compatible with references. The rule inside clientId is not evaluated (tested on CP 7.6.1)

## Shutdown

1. Stop the consumers and producers
2. Stop the environment

```shell
    cd env
    docker-compose down -v && docker system prune -f
    cd ..
```

References:
- Code
  -