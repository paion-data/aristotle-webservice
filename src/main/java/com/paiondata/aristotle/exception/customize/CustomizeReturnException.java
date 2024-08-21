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
package com.paiondata.aristotle.exception.customize;

import com.paiondata.aristotle.common.base.ReturnCode;
import com.paiondata.aristotle.exception.CustomizeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * Custom exception that wraps a return code and a message.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeReturnException extends CustomizeException {

    /**
     * Constructs a new CustomizeReturnException with default return code and message.
     */
    public CustomizeReturnException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    /**
     * Constructs a new CustomizeReturnException with a specific return code.
     *
     * @param returnCode the return code
     */
    public CustomizeReturnException(final ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    /**
     * Constructs a new CustomizeReturnException with a specific return code and a custom message.
     *
     * @param returnCode the return code
     * @param msg        the custom message
     */
    public CustomizeReturnException(final ReturnCode returnCode, final String msg) {
        this.returnCode = returnCode;
        this.msg = StringUtils.isBlank(msg) ? returnCode.getMsg() : returnCode.getMsg() + " ==> [" + msg + "]";
    }

    /**
     * Returns the message associated with this exception.
     * <p>
     * the message
     */
    @Override
    public String getMessage() {
        return this.msg;
    }
}
