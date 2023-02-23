# Read Me
This project is designed for an e-Commerce platform.

# Getting Started

## Setting up

- First create the DataBase "anymind" and provide the login details to the application.properties file.
- Create Table by executing SQL command in "resources/data.sql" file.
- Run the application.
- You can create the coverage report by executing "mvn clean install".
- Report can be found in "target/site/index.html" path.
- You can trigger the API calls by using GraphQL UI.

## GraphQL UI

http://localhost:8080/graphiql?path=/graphql

## Mutation
### To add the sales record and get the final price & points

```graphql
mutation {
  makePayment(payment: {
    price: "234.00",
    priceModifier: 0.90,
    paymentMethod: VISA,
    dateTime: "2022-10-01T15:45:43Z"
  }) {
    finalPrice,
    points
  }
}
```

## Query
### how much sales were made within a date range broken down into hours 

```graphql
query {
  sales(request: {
    startDateTime: "2022-09-01T00:00:00Z",
    endDateTime: "2022-09-01T23:59:59Z"
  }) {
    dateTime,
    sales,
    points
  }
}
```

