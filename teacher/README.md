## Commands

### Players

Get all players:

```shell
curl -X GET -H 'Content-Type: application/json' -i http://localhost:8080/players
```

Create a player

```shell
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8080/players --data '{ "name" : "John" }'
```

Delete a player

```shell
curl -X DELETE -H 'Content-Type: application/json' -i http://localhost:8080/players/2b6d41fa-9813-4152-b8e9-8f5309b27d75
```

### Battle

Get all battles:

```shell
curl -X GET -H 'Content-Type: application/json' -i http://localhost:8080/battles
```

Create a battle

```shell
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8080/battles --data '{ "player1Id" : "60f6b027-e42c-4dc4-9aa5-2d33da7b66ef", "player2Id" : "1ee7fc53-8d3e-41eb-a414-5122d5690645" }'
```

Delete a battle

```shell
curl -X DELETE -H 'Content-Type: application/json' -i http://localhost:8080/battles/2b6d41fa-9813-4152-b8e9-8f5309b27d75
```

