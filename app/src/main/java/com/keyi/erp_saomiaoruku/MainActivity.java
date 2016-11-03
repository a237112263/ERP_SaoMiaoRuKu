package com.keyi.erp_saomiaoruku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.keyi.erp_saomiaoruku.adapter.ListViewAdapter;
import com.keyi.erp_saomiaoruku.application.BaseActivity;
import com.keyi.erp_saomiaoruku.bean.StockNoMsg;
import com.keyi.erp_saomiaoruku.interfaces.MsgView;
import com.keyi.erp_saomiaoruku.present.MsgPresent;
import com.keyi.erp_saomiaoruku.utils.DatasUtils;
import com.keyi.erp_saomiaoruku.view.SMSActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MsgView, View.OnClickListener {
    public static Activity activity;
    @Bind(R.id.lv_main)
    ListView listView;
    @Bind(R.id.tv_main)
    TextView textView;
    @Bind(R.id.tv_shuaxin)
    TextView shuaxin;
    @Bind(R.id.iv_main1)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.activity = this;
        MsgPresent msgPresent = new MsgPresent(this, 0);
        msgPresent.getMsg(DatasUtils.getStockNoMsgUrl(this));
        shuaxin.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void isTrue(String istrue, int queneFlag) {
        if (queneFlag == 0) {
            Gson gson = new Gson();
            StockNoMsg stockNoMsg = gson.fromJson(istrue, StockNoMsg.class);
            if (stockNoMsg.isIsOK()) {
                ListViewAdapter adapter = new ListViewAdapter(this, stockNoMsg);
                listView.setAdapter(adapter);
            } else {
                if (stockNoMsg.getErrMsg().toString().equals("手机号码不正确或者登录超时,请重新登录!")) {
                    textView.setVisibility(View.VISIBLE);
                    listView.setEmptyView(textView);
                    new AlertDialog.Builder(this).setMessage("验证码已失效，请重新登录！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.activity.finish();
                                    Intent intent = new Intent(MainActivity.this, SMSActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();

                } else {
                    textView.setVisibility(View.VISIBLE);
                    listView.setEmptyView(textView);
                }
            }
        }
    }

    @Override
    public void isError(String isError, int queneFlag) {
        Toast.makeText(MainActivity.this, "服务器或网络异常！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_main1:
                Intent intent3 = new Intent();
                intent3.setClass(MainActivity.this, SMSActivity.class);
                intent3.putExtra("smsactivity", true);
                startActivity(intent3);
                break;
            case R.id.tv_shuaxin:
                MsgPresent msgPresent = new MsgPresent(this, 0);
                msgPresent.getMsg(DatasUtils.getStockNoMsgUrl(this));
                break;

        }
    }
}
