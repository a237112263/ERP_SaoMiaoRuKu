package com.keyi.erp_saomiaoruku.application;

import android.app.Application;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.keyi.erp_saomiaoruku.utils.IsNetworkAvailable;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2016/6/6.
 *
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
        Bmob.initialize(this, "a66ad8ab7ed32dd6828d5e3d23c2f1eb");
        IsNetworkAvailable available=new IsNetworkAvailable();
        if (available.isNetworkAvailable(MyApplication.this)){

        }else {
            Toast.makeText(getApplicationContext(),"网络连接错误,请检查网络！",Toast.LENGTH_SHORT).show();
        }
    }
}
