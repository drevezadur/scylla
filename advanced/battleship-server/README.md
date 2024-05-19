
```shell
curl -X POST localhost:11000/battleship/players -H 'Content-Type: application/json' -d '{"name":"sherlock"}'
```

```shell
curl -sw '\n%{http_code}' -X POST http://localhost:11000/battleship/players -H 'Content-Type: application/json' -d '{"name":"Greg"}' -v
```