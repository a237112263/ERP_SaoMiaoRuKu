package com.keyi.erp_saomiaoruku.scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.keyi.erp_saomiaoruku.MainActivity;
import com.keyi.erp_saomiaoruku.R;
import com.keyi.erp_saomiaoruku.bean.IsFinished;
import com.keyi.erp_saomiaoruku.bean.IsScaned;
import com.keyi.erp_saomiaoruku.interfaces.MsgView;
import com.keyi.erp_saomiaoruku.present.MsgPresent;
import com.keyi.erp_saomiaoruku.scanner.camera.CameraManager;
import com.keyi.erp_saomiaoruku.scanner.decoding.CaptureActivityHandler;
import com.keyi.erp_saomiaoruku.scanner.decoding.InactivityTimer;
import com.keyi.erp_saomiaoruku.scanner.view.ViewfinderView;
import com.keyi.erp_saomiaoruku.utils.ACache;
import com.keyi.erp_saomiaoruku.utils.DatasUtils;

import java.io.IOException;
import java.util.Vector;

public class CaptureActivity extends Activity implements Callback, MsgView {
    public static final String QR_RESULT = "RESULT";

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;

    // private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    CameraManager cameraManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_capture);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinderview);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // CameraManager.init(getApplication());
        cameraManager = new CameraManager(getApplication());

        viewfinderView.setCameraManager(cameraManager);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            // CameraManager.get().openDriver(surfaceHolder);
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        //showResult(obj, barcode);
        storeResult(obj);
    }

    private void storeResult(final Result rawResult) {
        Intent intent = getIntent();
        String resutlt = rawResult.toString();
        String[] msg = resutlt.split("\\$");

        ACache aCache = ACache.get(this);
        if (intent.getIntExtra("flag", 1) == 0) {
            //入库单号
            String stockNo = intent.getStringExtra("stockNo").toString();
            String subStockNo = stockNo.substring(stockNo.length() - 8, stockNo.length());
            String newStr = subStockNo.replaceAll("^(0+)", "");
            //标签的入库单号
            aCache.put("newStr", newStr);//截取后的入库单号
            aCache.put("stockNo", stockNo);//入库单号
        }
        setStockNo(aCache.getAsString("stockNo"), aCache.getAsString("newStr"), msg);
    }

    private void setStockNo(String stockNo, String newStr, String[] msg) {
        ACache aCache = ACache.get(this);
        if (msg.length == 4) {
            if (msg[1].equals("KYSOFT")) {
                aCache.put("tag", msg[0]);
                if (newStr.equals(msg[3])) {
                    MsgPresent msgPresent = new MsgPresent(CaptureActivity.this, 2);
                    msgPresent.getMsg(DatasUtils.isScanned(this, stockNo, msg[0]));
                } else {
                    Toast.makeText(CaptureActivity.this, "入库单没有该标签", Toast.LENGTH_SHORT).show();
                    playSound(R.raw.fail);
                    restartPreviewAfterDelay(2000L);
                }
            } else {
                Toast.makeText(CaptureActivity.this, "入库单没有该标签", Toast.LENGTH_SHORT).show();
                playSound(R.raw.fail);
                restartPreviewAfterDelay(2000L);
            }
        } else {
            Toast.makeText(CaptureActivity.this, "入库单没有该标签", Toast.LENGTH_SHORT).show();
            playSound(R.raw.fail);
            restartPreviewAfterDelay(2000L);
        }
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(MessageIDs.restart_preview, delayMS);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            try {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                this.mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    @Override
    public void isTrue(String istrue, int queneFlag) {
        ACache aCache = ACache.get(this);
        Gson gson = new Gson();
        if (queneFlag == 2) {
            Log.e("isscan", istrue);
            IsScaned isScaned = gson.fromJson(istrue, IsScaned.class);
            if (isScaned.isIsOK()) {
                Toast.makeText(CaptureActivity.this, "扫描成功", Toast.LENGTH_SHORT).show();
                playSound(R.raw.sucee);
                MsgPresent msgPresent = new MsgPresent(CaptureActivity.this, 4);
                msgPresent.getMsg(DatasUtils.isScanned(this, aCache.getAsString("stockNo"), aCache.getAsString("tag")));
                restartPreviewAfterDelay(2000L);
            } else {
                if (isScaned.getErrMsg().toString().equals("该标签已扫描过!")) {
                    playSound(R.raw.fail);
                    Toast.makeText(CaptureActivity.this, "该标签已扫描过", Toast.LENGTH_SHORT).show();
                    restartPreviewAfterDelay(2000L);
                }
            }
        }
        if (queneFlag == 4) {
            Log.e("isscan", istrue);
            IsScaned isScaned = gson.fromJson(istrue, IsScaned.class);
            if (isScaned.getErrMsg().toString().equals("商品已全部扫完！")) {

                playSound(R.raw.ok);
                MsgPresent msgPresent = new MsgPresent(CaptureActivity.this, 1);//如果已全部扫完，则自动完结掉入库单
                msgPresent.getMsg(DatasUtils.finishUrl(this, aCache.getAsString("stockNo")));
            }
        }

        if (queneFlag == 1) {
            IsFinished isFinished = gson.fromJson(istrue, IsFinished.class);
            if (isFinished.isIsOK()) {
                MainActivity.activity.finish();
                Intent intent = new Intent(CaptureActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {

            }
        }
    }

    @Override
    public void isError(String isError, int queneFlag) {
        Toast.makeText(CaptureActivity.this, "服务器或网络异常！", Toast.LENGTH_SHORT).show();
    }

    private void playSound(int res) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        AssetFileDescriptor file = this.getResources().openRawResourceFd(
                res);//声音
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.prepare();
        } catch (IOException ioe) {

        }
        mediaPlayer.start();
    }
}