package com.paiondata.aristotle

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