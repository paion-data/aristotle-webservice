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
package com.paiondata.aristotle.service;

import java.util.List;
import java.util.Map;

public interface Neo4jService {

    List<Map<String, Object>> getUserAndGraphsByUidcid(String uidcid);

    List<Map<String, Map<String, Object>>> getGraphNodeByGraphUuid(String uuid);

    void updateGraphByUuid(String uuid, String title, String description, String updateTime);

    void updateNodeByUuid(String uuid, String title, String description, String updateTime);
}
