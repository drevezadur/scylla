/*
 * Copyright (c)  2023-2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezadur.scylla.teacher.restserver.testutils

object PlayScripts {
    val CREATE_PLAYERS_AND_BATTLE_SCRIPT = """
                createPlayer John
                createPlayer Jane
                
                createBattle John Jane
        """.trimIndent()

    val DEPLOY_JOHN_FLEET_SCRIPT = """
                deployShip John CARRIER     00  ROW
                deployShip John BATTLESHIP  01  ROW
                deployShip John CRUISER     02  ROW
                deployShip John SUBMARINE   03  ROW
                deployShip John DESTROYER   04  ROW
        """.trimIndent()

    val DEPLOY_JANE_FLEET_SCRIPT = """
                deployShip Jane CARRIER     00  COLUMN
                deployShip Jane BATTLESHIP  10  COLUMN
                deployShip Jane CRUISER     20  COLUMN
                deployShip Jane SUBMARINE   30  COLUMN
                deployShip Jane DESTROYER   40  COLUMN                
        """.trimIndent()

    val FIGHT_UNTIL_JANE_VICTORY = """
                shot 00 # Jane Carrier 1/5
                shot 00 # John Carrier 1/5
                
                shot 01 # Jane Carrier 2/5
                shot 10 # John Carrier 2/5

                shot 02 # Jane Carrier 3/5
                shot 20 # John Carrier 3/5

                shot 03 # Jane Carrier 4/5 
                shot 30 # John Carrier 4/5

                shot 04 # Jane Carrier 5/5 
                shot 40 # John Carrier 5/5
                
                shot 06 # Jane water
                shot 01 # John Battleship 1/4
                
                shot 16 # Jane water
                shot 11 # John Battleship 2/4
                
                shot 26 # Jane water
                shot 21 # John Battleship 3/4
                
                shot 36 # Jane water
                shot 31 # John Battleship 4/4

                shot 46 # Jane water
                shot 02 # John Cruiser 1/3
                
                shot 56 # Jane water
                shot 12 # John Cruiser 2/3
                
                shot 66 # Jane water
                shot 22 # John Cruiser 3/3
                
                shot 76 # Jane water
                shot 03 # John Submarine 1/3
                
                shot 86 # Jane water
                shot 13 # John Submarine 2/3
                
                shot 96 # Jane water
                shot 23 # John Submarine 3/3
                
                shot 07 # Jane water
                shot 04 # John Destroyer 1/2
                
                shot 17 # Jane water
                shot 14 # John Destroyer 2/2
        """.trimIndent()
}