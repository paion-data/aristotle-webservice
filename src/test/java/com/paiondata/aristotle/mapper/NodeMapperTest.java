/*
 * Copyright 2024 Paion Data
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
package com.paiondata.aristotle.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paiondata.aristotle.mapper.impl.NodeMapperImpl;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests the NodeMapperImpl.getFilterProperties method.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NodeMapperTest {

    /**
     * Tests the NodeMapperImpl.getFilterProperties method.
     *
     * @param node              the node label
     * @param entriesString     the filter entries
     * @param expected          the expected result
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException   the illegal access exception
     * @throws NoSuchMethodException the no such method exception
     */
    @ParameterizedTest
    @CsvSource({
            "n, name:Peter, WHERE n.name = 'Peter'",
            "n, 'name:Peter,age:30', WHERE n.name = 'Peter' AND n.age = '30'",
            "n, '', 'WHERE '",
            "n, name:O'Connor, WHERE n.name = 'O''Connor'",
            "n, 'name:O\"Connor', WHERE n.name = 'O\"Connor'",
            "n, 'name:O\"Connor,age:30', WHERE n.name = 'O\"Connor' AND n.age = '30'"
    })
    void testGetFilterProperties(final String node, final String entriesString, final String expected)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final String[] keyValuePairs = entriesString.split(",(?![^()]*\\))");
        final Map<String, String> entries = new HashMap<>();
        for (final String pair : keyValuePairs) {
            final String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                entries.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        final Method method = NodeMapperImpl.class.getDeclaredMethod("getFilterProperties", String.class, Map.class);
        method.setAccessible(true);

        final StringBuilder resultBuilder = (StringBuilder) method.invoke(null, node, entries);
        final String result = resultBuilder.toString();
        assertEquals(expected, result);
    }
}
