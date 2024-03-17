# Use-cases

## Player management

### Definition

A player is composed by:

* a unique identifier
* a unique name

The identifier is set by the server.
The name is set by the client

```json
{
  "id": "13-234-648",
  "name": "John"
}
```

### List players

Get the list of players registered in the server.
The list contains only the player ids.
For instance:

#### Request
```http request
GET http://localhost:11000/battleship/players
```


| URL      | Verb  | Content type | Body |
|----------|-------|--------------|------|
| /players | GET   | none         | no   |

**Example:**



```shell
$ curl http://localhost:11000/battleship/players
```

#### Responses

Types of responses:

| HTTP Code | Content type             | Meaning            |
|-----------|--------------------------|--------------------|
| 200       | application/json         | OK, list provided  |
| 500       | application/problem+json | Unexpected trouble |

**Response content for 200:**

```json
[
  {
    "id": "72-234-648",
    "name": "John"
  },
  {
    "id": "13-232-936",
    "name": "Jane"
  },
  {
    "id": "49-628-329",
    "name": "Soaraya"
  }
]
```


### Create player

Create a new player



#### Request

| URL      | Verb | Content type     |
|----------|------|------------------|
| /players | POST | application/json |

**Body:**

```json
{
  "name": "Walter"
}
```

**Attributes:**

| Attribute | Type   | Mandatory | Description            |
|-----------|--------|-----------|------------------------|
| name      | String | Yes       | The name of the player |


#### Responses

Types of responses:

| HTTP Code | Content type             | Meaning            |
|-----------|--------------------------|--------------------|
| 201       | none                     | OK, player created |
| 500       | application/problem+json | Unexpected trouble |


**Response 201:** 
Player created.
Return the URI in the header:
```text
location: http://localhost:11000/battleship/players/123-456
```



### Update player

#### Request

**Attributes:**

| Attribute | Type   | Mandatory | Description            |
|-----------|--------|-----------|------------------------|
| name      | String | no        | The name of the player |




### Delete player





