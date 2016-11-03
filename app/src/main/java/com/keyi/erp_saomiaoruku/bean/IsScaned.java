package com.keyi.erp_saomiaoruku.bean;

/**
 * Created by Administrator on 2016/9/24.
 */
public class IsScaned {

    /**
     * IsOK : false
     * ErrMsg : 手机号码不正确或者登录超时,请重新登录!
     * Data : null
     */

    private boolean IsOK;
    private String ErrMsg;
    private Object Data;

    public boolean isIsOK() {
        return IsOK;
    }

    public void setIsOK(boolean IsOK) {
        this.IsOK = IsOK;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String ErrMsg) {
        this.ErrMsg = ErrMsg;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object Data) {
        this.Data = Data;
    }
}
