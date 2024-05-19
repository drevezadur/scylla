rootProject.name = "advanced-scylla"

include( "battleship-lang")

include("battleship-domain")
include("battleship-persistance")
include("battleship-server")

include("battleship-scenario-dsl")
include("battleship-scenario-dsl-grammar")

include("battleship-scenario-dsl-domain-driver")
include("battleship-scenario-dsl-rest-driver")

include("server-integration-tests")
