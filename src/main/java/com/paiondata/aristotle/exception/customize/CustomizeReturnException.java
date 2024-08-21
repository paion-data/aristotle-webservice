package com.paiondata.aristotle.exception.customize;

import com.paiondata.aristotle.common.base.ReturnCode;
import com.paiondata.aristotle.exception.CustomizeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeReturnException extends CustomizeException {

    public CustomizeReturnException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public CustomizeReturnException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeReturnException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = StringUtils.isBlank(msg) ? returnCode.getMsg() : returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}