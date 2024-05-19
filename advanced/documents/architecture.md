# Advanced Scylla Architecture


## Modules
### battleship-lang
This module contains the basic types & domain concepts of the game.
These types have been separated from the battleship-domain because they are used in other modules (Persistence, DLC, integration).


### battleship-domain
This module contains all the domain logic of the game.

**Dependencies:**
* battleship-lang


### battleship-persistance
This module contains the persistance part of the game.

**Dependencies:**
* battleship-lang
* battleship-persistance


### battleship-scenario-dsl-grammar
This module contains the grammar file describing the execution of a scenario.
The binary contains also the ANTLR generated code for parsing a scenario file.

**Dependencies:** *none*



### battleship-scenario-dsl
This module is an interface for parsing a scenario file.
It hides the technologies (ANTLR) used to build a scenario DSL parser. 

**Dependencies:**
* battleship-scenario-dsl-grammar


### battleship-server
This module is the application server which contains an HTTP layer and assembles the modules required for execution.

**Dependencies:**
* battleship-lang
* battleship-domain
* battleship-persistance


### battleship-scenario-dsl-domain-driver
This module is used for running scenario directly on *battleship-domain*.

### integration-tests
This module is a set of integration tests that are interfaced with the server via HTTP protocole.  

**Dependencies:**
* battleship-lang
* battleship-scenario-dsl

**HTTP dependencies:**
* battleship-server
