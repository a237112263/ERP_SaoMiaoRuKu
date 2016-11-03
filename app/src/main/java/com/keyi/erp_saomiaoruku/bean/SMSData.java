package com.keyi.erp_saomiaoruku.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/5/16.
 */
public class SMSData implements Serializable{
    private static final long serialVersionUID = -1L;
    /**
     * IsOK : true
     * ErrMsg : null
     * Data : BA83517C5C760D708B41D69A6B8DE479D77EE921348BA06A4D0866C7AC3E58F2AEAFE3C17F00E617767EB15189A9C6F4B469E4759F8E44C45D4931E21309B2554BC38DE67CB60ED3FDCE1B54E9590B8FF3F3D57140889EF2A43E7DCE5F2E61B5
     */

    private boolean IsOK;
    private Object ErrMsg;
    private String Data;

    public boolean isIsOK() {
        return IsOK;
    }

    public void setIsOK(boolean IsOK) {
        this.IsOK = IsOK;
    }

    public Object getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(Object ErrMsg) {
        this.ErrMsg = ErrMsg;
    }

    public String getData() {
        return Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }
}
