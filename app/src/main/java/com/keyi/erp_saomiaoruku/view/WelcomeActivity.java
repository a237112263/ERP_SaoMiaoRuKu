package com.keyi.erp_saomiaoruku.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.keyi.erp_saomiaoruku.MainActivity;
import com.keyi.erp_saomiaoruku.R;
import com.keyi.erp_saomiaoruku.bean.ERP_SaoMiaoRuKu;
import com.keyi.erp_saomiaoruku.bean.SMSData;
import com.keyi.erp_saomiaoruku.interfaces.MsgView;
import com.keyi.erp_saomiaoruku.present.MsgPresent;
import com.keyi.erp_saomiaoruku.utils.ACache;
import com.keyi.erp_saomiaoruku.utils.DatasUtils;
import com.keyi.erp_saomiaoruku.utils.UpdateManager;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener, MsgView {
    private ImageView imageView;

    @Override
    protected void onStart() {
        super.onStart();
        BmobQuery<ERP_SaoMiaoRuKu> bmobQuery=new BmobQuery<ERP_SaoMiaoRuKu>();
        bmobQuery.getObject(this, "KJeW666F", new GetListener<ERP_SaoMiaoRuKu>() {
            @Override
            public void onSuccess(ERP_SaoMiaoRuKu person) {
                final UpdateManager manager = new UpdateManager(WelcomeActivity.this);
                Log.e("ASDASDD", person.getVisioncode().toString());
                // 获取当前软件版本
                int versionCode = getVersionCode(WelcomeActivity.this);
                Log.e("versionCode", versionCode + "");
                if (versionCode < Integer.parseInt(person.getVisioncode().toString())) {
                    manager.checkUpdate();
                } else {

                }
            }
            @Override
            public void onFailure(int i, String s) {
                Log.e("s", s.toString());

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        imageView= (ImageView) findViewById(R.id.iv_welcome);
        imageView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        final ACache aCache=ACache.get(this);
        SharedPreferences setting = getSharedPreferences("SHARE_APP_TAG", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if(user_first){//第一次
            setting.edit().putBoolean("FIRST", false).commit();
            Intent intent=new Intent(WelcomeActivity.this,SMSActivity.class);
            startActivity(intent);
            finish();
        }else{
            MsgPresent loadMsgModel = new MsgPresent(this,0);

            final String url = DatasUtils.mobUrl+  aCache.getAsString("MobilNumber")+"&code="+aCache.getAsString("yanzhengma");
            Log.e("url",url);
            loadMsgModel.getMsg(url);
        }
    }
    private int getVersionCode(Context context)
    {
        int versionCode = 0;
        try
        {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.keyi.erp_saomiaoruku", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return versionCode;
    }

    @Override
    public void isTrue(String data, int queneFlag) {
        Gson gson=new Gson();
        SMSData smsData=gson.fromJson(data,SMSData.class);
        if (smsData.isIsOK()==true){
            DatasUtils.inTent(WelcomeActivity.this,MainActivity.class);
            finish();
        }else {
            DatasUtils.inTent(WelcomeActivity.this, YanZhengMaAtivity.class);
            finish();
        }
    }

    @Override
    public void isError(String isError, int queneFlag) {

    }
}
