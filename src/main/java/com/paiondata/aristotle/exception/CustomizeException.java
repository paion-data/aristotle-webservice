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
package com.paiondata.aristotle.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.paiondata.aristotle.common.base.ReturnCode;

/**
 * Custom exception that wraps a return code and a message.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeException extends RuntimeException {

    /**
     * The return code associated with this exception.
     */
    public ReturnCode returnCode = null;

    /**
     * The message associated with this exception.
     */
    public String msg = null;
}
