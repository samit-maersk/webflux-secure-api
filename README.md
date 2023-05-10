# webflux-secure-api

- This api was build using Spring Webflux 
- It was secured with spring-boot-starter-oauth2-resource-server. 
- This service/api need a Bearer token with role ADMIN or USER.
- `spring-boot-starter-oauth2-resource-server` need a jwk-set-uri endpoint to validate the token.

To run this api you need to run the following command:

```shell
 ./mvnw spring-boot:run
```

Before that we need a authorisation server with jwt and jwk-set-uri endpoint.
To make this happen:
- Checkout this [Repository](https://github.com/samit-maersk/jwt-jwks-endpoint.git)
- Run the following command:

```shell
 git clone https://github.com/samit-maersk/jwt-jwks-endpoint.git
 npm install
 npm start
```
The jwk-set-uri endpoint will be available at `http://localhost:3000/oauth2/jwks` mention this in the application.yml file.
To get the token you need to run the following command:

```shell
  #Token with role USER
  curl http://localhost:3000/oauth2/token?type=user
  
  #Token with role ADMIN & USER
  curl http://localhost:3000/oauth2/token?type=admin
```

> There are Junit test cases to test the api, method level security and global security.