# vehicle-status

The Vehicle Status Checker is a backend component that helps to check Vehicle details by VIN

## Build
The application can be built following command using CLI
```
mvn clean install
```

## Run
The component can be started through the following two commands in two different CLI sessions
(Default port on which it starts is 8080)

Command #1 is to start the application
```
mvn mn:run
```

Command #2 can be executed in a second CLI window in order to start the WireMock
```
mvn wiremock:run
```

## Example cURL request is

```
curl --location --request POST 'http://localhost:8080/check' \
--header 'Content-Type: application/json' \
--data-raw '{
  "vin":"4Y1SL65848Z411439",
  "features":[
    "accident_free",
	  "maintenance"
	]
}'
```


Where:
* 'vin' parameter is mandatory with 3-64 chars legth
* 'features' parameter is a set with possible values: accident_free, maintenance

Note: Note: You can manipulate the mocked responses of the external services on which the application depends in folder src/test/resources/mappings (Please rebuild the component and restart both - the application  and WireMock in order to apply any changes in this folder
