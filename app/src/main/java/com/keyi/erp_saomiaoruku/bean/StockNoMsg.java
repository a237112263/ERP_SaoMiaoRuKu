package com.keyi.erp_saomiaoruku.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/24.
 */
public class StockNoMsg {
    /**
     * IsOK : true
     * ErrMsg : 请求成功！
     * Data : [{"StockNo":"RT16091200000593"},{"StockNo":"RT16091200000594"},{"StockNo":"RT16091200000597"},{"StockNo":"RT16091200000600"},{"StockNo":"RT16091800000601"},{"StockNo":"RT16091800000602"},{"StockNo":"RT16091800000603"},{"StockNo":"RT16091800000604"},{"StockNo":"RT16091900000605"},{"StockNo":"RT16091900000607"},{"StockNo":"RT16091900000608"},{"StockNo":"RT16091900000609"},{"StockNo":"RT16091900000610"},{"StockNo":"RT16091900000611"},{"StockNo":"RT16091900000612"},{"StockNo":"RT16092000000613"},{"StockNo":"RT16092000000617"},{"StockNo":"RT16092100000618"},{"StockNo":"RT16092100000619"},{"StockNo":"RT16092100000620"},{"StockNo":"RT16092100000621"},{"StockNo":"RT16092100000622"},{"StockNo":"RT16092100000623"},{"StockNo":"RT16092100000624"},{"StockNo":"RT16092100000625"},{"StockNo":"RT16092100000626"},{"StockNo":"RT16092200000627"},{"StockNo":"RT16092200000628"},{"StockNo":"RT16092200000629"},{"StockNo":"RT16092200000630"},{"StockNo":"RT16092200000631"},{"StockNo":"RT16092300000632"},{"StockNo":"RT16092300000635"},{"StockNo":"RT16092300000636"},{"StockNo":"RT16092300000637"},{"StockNo":"RT16092300000638"}]
     */
    private boolean IsOK;
    private String ErrMsg;
    /**
     * StockNo : RT16091200000593
     */

    private List<DataBean> Data;

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

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        private String StockNo;

        public String getStockNo() {
            return StockNo;
        }

        public void setStockNo(String StockNo) {
            this.StockNo = StockNo;
        }
    }
}
