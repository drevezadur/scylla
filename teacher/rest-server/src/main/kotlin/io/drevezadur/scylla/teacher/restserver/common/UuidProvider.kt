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

package io.drevezadur.scylla.teacher.restserver.common

import java.util.*

/**
 * A provider of UUID in the application
 *
 * ALWAYS use this service to manipulate UUIDs
 */
interface UuidProvider {

    /**
     * Generate a new UUID
     * @return a UUID
     */
    fun create(): UUID

    /**
     * Convert a string to UUID format
     * @param uuidAsString the textual representation of a UUD
     * @return the UUID format of the argument
     * @throws IllegalArgumentException if [uuidAsString] is not a valid textual representation of a UUID
     */
    fun fromString(uuidAsString: String): UUID


    /**
     * Convert a string to a UUID format
     * @param uuidAsString the textual representation of a UUD
     * @param failureHandler handle the case when [uuidAsString] is not a valid textual representation of a UUID
     * @return the UUID format of the argument when it is a valid UUID
     */
    fun fromString(uuidAsString: String, failureHandler: (uuid: String) -> Nothing): UUID

}