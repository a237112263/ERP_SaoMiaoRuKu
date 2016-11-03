package com.keyi.erp_saomiaoruku.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.keyi.erp_saomiaoruku.MainActivity;
import com.keyi.erp_saomiaoruku.R;
import com.keyi.erp_saomiaoruku.bean.SMSData;
import com.keyi.erp_saomiaoruku.utils.ACache;
import com.keyi.erp_saomiaoruku.utils.DatasUtils;
import com.keyi.erp_saomiaoruku.utils.HttpUtils;
import com.robin.lazy.sms.SmsObserver;
import com.robin.lazy.sms.SmsResponseCallback;
import com.robin.lazy.sms.VerificationCodeSmsFilter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/27.
 */
public class SMSActivity extends Activity implements View.OnClickListener, SmsResponseCallback {
    private Button buttonYanzheng,buttonDenglu,buttonJishi;
    private EditText editTextMobilNumber,editTextYanzheng;
    private int i=0;
    private ACache aCache;
    private SmsObserver smsObserver;
    private static int MSG_DISMISS_DIALOG = 0;
    private ProgressDialog dialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 2:
                    if (i == 0) {
                        buttonYanzheng.setVisibility(View.VISIBLE);
                    } else {
                        buttonYanzheng.setVisibility(View.INVISIBLE);
                        buttonJishi.setText(i-- + "秒后重新发送");
                    }
                    break;
                case 0:
                    if (dialog!=null){
                        if (dialog.isShowing()){
                            dialog.dismiss();
                            finish();
                        }
                    }
            }
        }
    };

        //验证延时60S后继续点击BUTTON
        Timer timer1 = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sms);
            editTextMobilNumber = (EditText) findViewById(R.id.et_sms1);
            editTextYanzheng = (EditText) findViewById(R.id.et_sms2);
            smsObserver=new SmsObserver(this,this,new VerificationCodeSmsFilter("106"));
            smsObserver.registerSMSObserver();
            Dexter.checkPermission(new CompositePermissionListener(), Manifest.permission.READ_SMS);
            aCache=ACache.get(this);
            initView();
        }

        private void initView() {
            if (aCache.getAsString("MobilNumber")!=null){
                editTextMobilNumber.setText(aCache.getAsString("MobilNumber").toString());
            }
            buttonYanzheng = (Button) findViewById(R.id.bt_sms1);
            buttonDenglu = (Button) findViewById(R.id.bt_sms2);
            buttonJishi = (Button) findViewById(R.id.bt_sms3);
            buttonYanzheng.setOnClickListener(this);
            buttonDenglu.setOnClickListener(this);
            buttonJishi.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_sms1:
                    if (editTextMobilNumber.getText().length() == 11) {
                        i = 60;
                        Toast.makeText(SMSActivity.this, "短信发送成功!", Toast.LENGTH_SHORT).show();
                        try {
                            timer1.schedule(task1, 1000, 1000);
                        } catch (Exception e) {
                        };
                        yanzhengHttp();
                    } else {
                        Toast.makeText(SMSActivity.this, "手机号码输入错误", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.bt_sms2:
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("正在登录中,请稍等...");
                    dialog.show();
                    buttonDenglu.setClickable(false);
                    dengluHttp();
                    break;
            }
        }


    private void dengluHttp() {
        final String url = DatasUtils.mobUrl + editTextMobilNumber.getText().toString()+"&code="+editTextYanzheng.getText().toString();
        Log.e("smsData",url+"");
        HttpUtils utils = new HttpUtils();
        utils.getJson(url, new HttpUtils.HttpCallBack() {
            @Override
            public void onSusscess(String data) {
                Gson gson=new Gson();
                SMSData smsData=gson.fromJson(data,SMSData.class);
                aCache.put("SMSData",smsData);
                Log.e("smsData",smsData.getData()+"");
                aCache.put("MobilNumber",editTextMobilNumber.getText().toString());
                aCache.put("yanzhengma", editTextYanzheng.getText().toString());
                if (smsData.isIsOK()==true){
                    smsObserver.unregisterSMSObserver();
                    if (getIntent().getBooleanExtra("smsactivity",false)){
                        MainActivity.activity.finish();
                    }
                    Toast.makeText(SMSActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(SMSActivity.this,MainActivity.class);
                    handler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG,4000);
                    startActivity(intent);
                }else {
                    handler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 2000);
                    Toast.makeText(SMSActivity.this, "验证码错误！", Toast.LENGTH_SHORT).show();
                    buttonDenglu.setClickable(true);
                }
            }

            @Override
            public void onError(String meg) {
                super.onError(meg);
                buttonDenglu.setClickable(true);
                Toast.makeText(SMSActivity.this, "网络连接错误！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void yanzhengHttp() {
            final String url = DatasUtils.yanzhengUrl + editTextMobilNumber.getText();
            HttpUtils utils = new HttpUtils();
            utils.getJson(url, new HttpUtils.HttpCallBack() {
                @Override
                public void onSusscess(String data) {
                    Log.i("msg", "请求成功"+url);
                }
            });
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer1.cancel();
    }
    @Override
    public void onCallbackSmsContent(String smsContent) {
        editTextYanzheng.setText(smsContent);
    }
}
