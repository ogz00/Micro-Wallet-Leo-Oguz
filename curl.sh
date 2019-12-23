curl -X POST http://localhost:8080/api/v1/currency \
-H 'Content-Type: application/json' \
-d '{
"name" :"United States Dollar",
"code": "USD"
}'

curl -X POST http://localhost:8080/api/v1/currency \
-H 'Content-Type: application/json' \
-d '{
"name" :"EURO",
"code": "EUR"
}'

curl -X GET http://localhost:8080/api/v1/currency




curl -X POST http://localhost:8080/api/v1/player \
-H 'Content-Type: application/json' \
-d '{
"name": "player1",
"country": "Test"
}'

curl -X POST http://localhost:8080/api/v1/player \
-H 'Content-Type: application/json' \
-d '{
"name": "player2",
"country": "Test"
}'


curl -X POST http://localhost:8080/api/v1/player \
-H 'Content-Type: application/json' \
-d '{
"name": "player1",
"country": "Test"
}'


curl http://localhost:8080/api/v1/player \
    -H 'Content-Type: application/json'

#Change Id according to response before

curl -X POST http://localhost:8080/api/v1/wallet \
-H 'Content-Type: application/json' \
-d '{
"playerId": 1,
"currencyId": 1
}'


curl http://localhost:8080/api/v1/wallet/1 \
    -H 'Content-Type: application/json'


curl -X POST http://localhost:8080/api/v1/transaction \
-H 'Content-Type: application/json' \
-d '{
    "suppliedId" : 1,
    "amount": "35.25",
    "walletId": 1,
    "transactionType": 1
}'

curl -X POST http://localhost:8080/api/v1/transaction \
-H 'Content-Type: application/json' \
-d '{
    "suppliedId" : 2,
    "amount": "12.25",
    "walletId": 1,
    "transactionType": 2
}'

curl http://localhost:8080/api/v1/wallet/1 \
    -H 'Content-Type: application/json'

curl http://localhost:8080/api/v1/wallet/1/transactions \
    -H 'Content-Type: application/json'

curl http://localhost:8080/api/v1/player/1/transactions \
    -H 'Content-Type: application/json'


curl http://localhost:8080/api/v1/transaction/1 \
    -H 'Content-Type: application/json'


# Invalid suppliedId error
curl -X POST http://localhost:8080/api/v1/transaction \
-H 'Content-Type: application/json' \
-d '{
    "suppliedId" : 2,
    "amount": "12.25",
    "walletId": 1,
    "transactionType": 2
}'

# Insufficient Balance error
curl -X POST http://localhost:8080/api/v1/transaction \
-H 'Content-Type: application/json' \
-d '{
    "suppliedId" : 3,
    "amount": "112.25",
    "walletId": 1,
    "transactionType": 2
}'
