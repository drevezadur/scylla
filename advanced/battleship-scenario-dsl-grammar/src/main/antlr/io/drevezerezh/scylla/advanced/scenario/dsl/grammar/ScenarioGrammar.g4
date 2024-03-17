grammar ScenarioGrammar;
@header {
package io.drevezerezh.scylla.advanced.scenario.dsl.grammar;
}
scenario: createPlayer* deployShip* shot* EOF;
createPlayer: 'create' 'player' PLAYER_NAME ;
deployShip: PLAYER_NAME 'deploy' SHIP 'at' LOCATION 'on' ORIENTATION ;
shot: PLAYER_NAME 'shot' 'at' LOCATION ;
LOCATION: [A-J][0-9] ;
ORIENTATION: 'row' | 'column';
SHIP: 'Carrier' | 'Battleship' | 'Cruiser' | 'Submarine' | 'Destroyer' ;
PLAYER_NAME: [a-zA-Z][a-zA-Z0-9\-]* ;
NEWLINE: [\r\n]+ -> skip ;
WS : [ \t\r\n]+ -> skip ;
