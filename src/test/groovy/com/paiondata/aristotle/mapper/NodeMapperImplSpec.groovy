/*
 * Copyright Paion Data
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
package com.paiondata.aristotle.mapper

import spock.lang.Specification
import com.paiondata.aristotle.mapper.impl.NodeMapperImpl

class NodeMapperImplSpec extends Specification {

    @SuppressWarnings('GroovyAccessibility')
    def "When user filter map is '#entries' on node '#node', the Neo4J filter clause gets translated to #expected"() {
        expect:
        NodeMapperImpl.getFilterProperties(node, entries).toString() == expected

        where:
        node | entries                            || expected
        "n"  | ["name": "Peter"]                  || "WHERE n.name = 'Peter'"
        "n"  | ["name": "Peter", "age": "30"]     || "WHERE n.name = 'Peter' AND n.age = '30'"
        "n"  | [:]                                || "WHERE "
        "n"  | ["name": "O'Connor"]               || "WHERE n.name = 'O''Connor'"
        "n"  | ["name": "O\"Connor"]              || "WHERE n.name = 'O\"Connor'"
        "n"  | ["name": "O'Connor", "age": "30"]  || "WHERE n.name = 'O''Connor' AND n.age = '30'"
        "n"  | ["name": "O\"Connor", "age": "30"] || "WHERE n.name = 'O\"Connor' AND n.age = '30'"
    }
}
