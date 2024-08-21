package com.paiondata.aristotle.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.paiondata.aristotle.common.base.ReturnCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeException extends RuntimeException {

    public ReturnCode returnCode = null;

    public String msg = null;

}
