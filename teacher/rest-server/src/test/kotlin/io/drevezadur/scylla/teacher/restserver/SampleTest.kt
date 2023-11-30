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

package io.drevezadur.scylla.teacher.restserver

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class SampleTest {

    @Test
    fun doTest1() {
        //Pattern.compile("deployShip ([a-zA-Z0-9]{1,20}) ([A-Z]{1,15}) ([0-9]{2}) (ROW|COLUMN)")
        val pattern = Pattern.compile("deployShip ([a-zA-Z0-9]{1,20}) ([A-Z]{1,15}) ([0-9]{2}) (ROW|COLUMN)")

        assertThat(pattern.matcher("deployShip John CARRIER 00 ROW").matches())
            .isTrue()
    }

    @Test
    fun doTest2() {
        //Pattern.compile("deployShip ([a-zA-Z0-9]{1,20}) ([A-Z]{1,15}) ([0-9]{2}) (ROW|COLUMN)")
        //val pattern = Pattern.compile("deployShip\\p{Blank}+([a-zA-Z0-9]{1,20})")
        val pattern = Pattern.compile("deployShip[ \\t]+([a-zA-Z0-9]{1,20})")

        assertThat(pattern.matcher("deployShip CARRIER").matches())
            .isTrue()
        assertThat(pattern.matcher("deployShip  CARRIER").matches())
            .isTrue()
        assertThat(pattern.matcher("deployShip\tCARRIER").matches())
            .isTrue()
        assertThat(pattern.matcher("deployShip\t \tCARRIER").matches())
            .isTrue()
    }

}