package com.keyi.erp_saomiaoruku.utils;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by CaoAnyang on 2016/4/11.
 */
public class HttpUtils {
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private OkHttpClient client;
    //超时时间
    public static final int TIMEOUT = 1000 * 60;

    //json请求
    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");

    private Handler handler = new Handler(Looper.getMainLooper());

    public HttpUtils() {
        this.init();
    }

    private void init() {
        client = new OkHttpClient();
        //设置超时
        client.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS).
                writeTimeout(TIMEOUT, TimeUnit.SECONDS).readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();

    }


    /**
     * post请求  json数据为body
     */
    public void postJson(String url, String json, final HttpCallBack callBack) {
        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder().url(url).post(body).build();

        OnStart(callBack);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string());
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }

    /**
     * post请求  map是body
     *
     * @param url
     * @param map
     * @param callBack
     */
    public void postMap(String url, Map<String, String> map, final HttpCallBack callBack) {
        FormBody.Builder builder = new FormBody.Builder();

        //遍历map
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string());
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }

    /**
     * post提交流
     *
     * @param url
     * @param s
     * @param callBack
     */
    public void postStream(String url, final String s, final HttpCallBack callBack) {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8(s);
            }
        };
        Request request = new Request.
                Builder().url(url).
                post(requestBody).
                build();
        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string());
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }

    /**
     * post提交文件
     *
     * @param url
     * @param file
     * @param callBack
     *
     */

    public void postFile(String url, File file, final HttpCallBack callBack) {

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string());
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }


    /**
     * get 请求
     *
     * @param url
     * @param callBack
     */
    public void getJson(String url, final HttpCallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        OnStart(callBack);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OnError(callBack, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callBack, response.body().string());
                } else {
                    OnError(callBack, response.message());
                }
            }
        });
    }

    public void OnStart(HttpCallBack callBack) {
        if (callBack != null) {
            callBack.onstart();
        }
    }

    public void onSuccess(final HttpCallBack callBack, final String data) {
        if (callBack != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {//在主线程操作
                    callBack.onSusscess(data);
                }
            });
        }
    }

    public void OnError(final HttpCallBack callBack, final String msg) {
        if (callBack != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(msg);
                }
            });
        }
    }

    public static abstract class HttpCallBack {
        //开始
        public void onstart() {

        };

        //成功回调
        public abstract void onSusscess(String data);

        //失败
        public void onError(String meg) {
        }

        ;
    }
}
