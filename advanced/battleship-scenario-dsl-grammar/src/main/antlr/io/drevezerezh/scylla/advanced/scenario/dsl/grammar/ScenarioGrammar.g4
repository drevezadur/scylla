/*
 * Copyright (c) 2024 gofannon.xyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

grammar ScenarioGrammar;
@header {
package io.drevezerezh.scylla.advanced.scenario.dsl.grammar;
}
scenario: createPlayer* startBattle* deployShip* shot* EOF;
createPlayer: 'create' 'player' PLAYER_NAME ;
startBattle: 'start' 'battle' 'with' PLAYER_NAME 'and' PLAYER_NAME ;
deployShip: PLAYER_NAME 'deploy' SHIP 'at' LOCATION 'on' ORIENTATION ;
shot: PLAYER_NAME 'shot' 'at' LOCATION ;
LOCATION: [A-J][0-9] ;
ORIENTATION: 'row' | 'column';
SHIP: 'Carrier' | 'Battleship' | 'Cruiser' | 'Submarine' | 'Destroyer' ;
PLAYER_NAME: [a-zA-Z][a-zA-Z0-9\-]* ;
NEWLINE: [\r\n]+ -> skip ;
WS : [ \t\r\n]+ -> skip ;
