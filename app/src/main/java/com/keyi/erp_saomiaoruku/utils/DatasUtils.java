package com.keyi.erp_saomiaoruku.utils;

import android.content.Context;
import android.content.Intent;

import com.keyi.erp_saomiaoruku.bean.SMSData;


/**
 * Created by Administrator on 2016/4/27.
 */
public class DatasUtils {
    public static final String strToken = "&Token=";
    public static final String yanzhengUrl = "http://appweb.keyierp.com/sms.aspx?m=";
    public static final String mobUrl = "http://appweb.keyierp.com/ERP/Login.aspx?mobile=";
    public static final String stockNoMsgUrl = "http://erpscan.keyierp.com/ERP_InStock/GetStockNos.aspx?mobile=";
    public static final String isScanned="http://erpscan.keyierp.com/ERP_InStock/IsScaned.aspx?mobile=";
    public static final String finishUrl="http://erpscan.keyierp.com/ERP_InStock/FinishStock.aspx?mobile=";

    public static final String tagCheckUrl="http://erpscan.keyierp.com/ERP_Stock/PrintTag.aspx?mobile=";
    public static final String SMSData(Context context) {
        ACache aCache = ACache.get(context);
        SMSData smsData = (SMSData) aCache.getAsObject("SMSData");
        return smsData.getData().toString();
    }

    public static final String MobilNumber(Context context) {
        ACache aCache = ACache.get(context);
        return aCache.getAsString("MobilNumber").toString();
    }

    public static final String getStockNoMsgUrl(Context context) {
        StringBuffer stringBuffer = new StringBuffer()
                .append(stockNoMsgUrl)
                .append(MobilNumber(context))
                .append(strToken)
                .append(SMSData(context));
        return stringBuffer.toString();
    }
    public static final String isScanned(Context context, String s,String tags) {
        StringBuffer stringBuffer = new StringBuffer()
                .append(isScanned)
                .append(MobilNumber(context))
                .append(strToken)
                .append(SMSData(context))
                .append("&StockNo=")
                .append(s)
                .append("&TagNo=")
                .append(tags);
        return stringBuffer.toString();
    }

    public static final String finishUrl(Context context, String s) {
        StringBuffer stringBuffer = new StringBuffer()
                .append(finishUrl)
                .append(MobilNumber(context))
                .append(strToken)
                .append(SMSData(context))
                .append("&StockNo=")
                .append(s);
        return stringBuffer.toString();
    }
    public static final String getTagCheckUrlUrl(Context context, String s) {
        StringBuffer stringBuffer = new StringBuffer()
                .append(tagCheckUrl)
                .append(MobilNumber(context))
                .append(strToken)
                .append(SMSData(context))
                .append("&Tag=")
                .append(s);
        return stringBuffer.toString();
    }

    public static final String numberZhuanHanzi(int number) {
        String s = null;
        switch (number) {
            case 1:
                s = "一";
                break;
            case 2:
                s = "二";
                break;
            case 3:
                s = "三";
                break;
            case 4:
                s = "四";
                break;
            case 5:
                s = "五";
                break;
            case 6:
                s = "六";
                break;
            case 7:
                s = "七";
                break;
            case 8:
                s = "八";
                break;
            case 9:
                s = "九";
                break;
        }
        return s;
    }
    public static final void inTent(Context context, Class o) {
        Intent intent = new Intent(context, o);
        context.startActivity(intent);
    }
}
