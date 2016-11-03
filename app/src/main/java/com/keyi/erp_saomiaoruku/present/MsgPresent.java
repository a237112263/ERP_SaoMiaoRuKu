package com.keyi.erp_saomiaoruku.present;


import com.keyi.erp_saomiaoruku.interfaces.MsgView;
import com.keyi.erp_saomiaoruku.utils.HttpUtils;

/**
 * Created by Administrator on 2016/8/1.
 */
public class MsgPresent {
    private MsgView msgView;
    private int queneFlag;

    public MsgPresent(MsgView msgView, int queneFlag) {
        this.msgView = msgView;
        this.queneFlag = queneFlag;
    }

    public void getMsg(String url) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.getJson(url, new HttpUtils.HttpCallBack() {
            @Override
            public void onSusscess(String data) {
                msgView.isTrue(data, queneFlag);
            }

            @Override
            public void onError(String meg) {
                super.onError(meg);
                msgView.isError(meg, queneFlag);
            }
        });
    }

}
