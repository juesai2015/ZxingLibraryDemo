package com.uuzuche.lib_zxing.activity;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.uuzuche.lib_zxing.R;
import com.uuzuche.lib_zxing.ScanTools;
import com.uuzuche.lib_zxing.camera.CameraManager;
import com.uuzuche.lib_zxing.decoding.CaptureActivityHandler;
import com.uuzuche.lib_zxing.decoding.InactivityTimer;
import com.uuzuche.lib_zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * 自定义实现的扫描Fragment
 */
public class CaptureFragment extends Fragment implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CodeUtils.AnalyzeCallback analyzeCallback;
    private Camera camera;
    private Camera.Parameters parameters;
    private Button btnChange;
    private Button btnFocus;
    private Button btnFlash;
    private Button btnAntibanding;
    private Button btnWhite;
    private Button btnScene;
    private ScanTools scanTools;
    private Button btnZoom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CameraManager.init(getActivity().getApplication());

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this.getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View view = null;
        if (bundle != null) {
            int layoutId = bundle.getInt(CodeUtils.LAYOUT_ID);
            if (layoutId != -1) {
                view = inflater.inflate(layoutId, null);
            }
        }

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_capture, null);
        }

        viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
        surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();


        btnChange = (Button) view.findViewById(R.id.btn_change);
        btnFocus = (Button) view.findViewById(R.id.btn_focus);
        btnFlash = (Button) view.findViewById(R.id.btn_flash);
        btnScene = (Button) view.findViewById(R.id.btn_scene);
        btnAntibanding = (Button) view.findViewById(R.id.btn_antibanding);
        btnWhite = (Button) view.findViewById(R.id.btn_white);
        btnZoom = (Button) view.findViewById(R.id.btn_zoom);
        change();
        return view;
    }

    private void change() {

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanTools.changeColor(btnChange);
            }
        });
        btnFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanTools.changeFocus(btnFocus);
            }
        });
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanTools.changeFlash(btnFlash);
            }
        });
        btnScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanTools.changeScene(btnScene);

            }
        });
        btnAntibanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parameters.setAntibanding(Camera.Parameters.ANTIBANDING_60HZ);
                camera.setParameters(parameters);
            }
        });
        btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanTools.changeWhite(btnWhite);
            }
        });
        btnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = parameters.getMaxZoom();
                int zoom = parameters.getZoom();
                Log.v("tag", max+"-"+zoom);
                scanTools.changeZoom(btnZoom);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
    }

    protected void openFlashlight() {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
    }


    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(barcode, result.getText());
            }
        }
    }



    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            camera = CameraManager.get().getCamera();
            parameters = camera.getParameters();
           // parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
            scanTools = ScanTools.newInstance(camera, parameters, getContext());
            camera.setParameters(parameters);

           // openFlashlight();

        } catch (Exception e) {
            if (callBack != null) {
                callBack.callBack(e);
            }
            return;
        }
        if (callBack != null) {
            callBack.callBack(null);
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet, viewfinderView);
        }
    }

    //设置相机滤镜
    private void setColor(String value){
        Camera.Parameters parameters = camera.getParameters();
        //设置滤镜模式
        parameters.setColorEffect(value);
        //设置对焦模式
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        //设置闪光灯
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

        camera.setParameters(parameters);
    }

    //设置相机滤镜
    private void setColors(String value){



        Camera.Parameters parameters = camera.getParameters();
        int i = camera.getParameters().getMaxExposureCompensation();
        Log.v("tag", "曝光"+i);
        camera.getParameters().setExposureCompensation(1);

        //-------------------------模式----------------
        List<String> colorEffects = parameters.getSupportedColorEffects();//扫描滤镜
        String colorEffect = parameters.getColorEffect();
        parameters.setColorEffect(value);

        List<String> focusModes = parameters.getSupportedFocusModes();//扫描对焦
        String focusMode = parameters.getFocusMode();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<String> flashModes = parameters.getSupportedFlashModes();//闪光灯
        String flashMode = parameters.getFlashMode();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);


        List<String> sceneModes = parameters.getSupportedSceneModes();//获得支持的场景模式
        String sceneMode = parameters.getSceneMode();
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_BEACH);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_CANDLELIGHT);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_FIREWORKS);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SNOW);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SUNSET);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_THEATRE);


        List<String> antibandings = parameters.getSupportedAntibanding();//获取支持的防牛顿环配置
        String antibanding = parameters.getAntibanding();
        parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
        parameters.setAntibanding(Camera.Parameters.ANTIBANDING_OFF);
        parameters.setAntibanding(Camera.Parameters.ANTIBANDING_50HZ);
        parameters.setAntibanding(Camera.Parameters.ANTIBANDING_60HZ);


        List<String> whiteBalances = parameters.getSupportedWhiteBalance();//获取当前支持的白平衡值
        String whiteBalance = parameters.getWhiteBalance();
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_SHADE);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_TWILIGHT);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT);


        //-------------------------数量----------------
        List<Camera.Size> jpegThumbnailSizes = parameters.getSupportedJpegThumbnailSizes();
        Camera.Size jpegThumbnailSize = parameters.getJpegThumbnailSize();
        parameters.setJpegThumbnailQuality(0);
        parameters.setJpegThumbnailSize(0,0);


        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size pictureSize = parameters.getPictureSize();
        parameters.setPictureSize(0,0);


        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = parameters.getPreviewSize();
        parameters.setPreviewSize(0,0);


        List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
        Camera.Size preferredPreviewSizeForVideo = parameters.getPreferredPreviewSizeForVideo();


        //-------------------------Formats-----------
        List<Integer> pictureFormats = parameters.getSupportedPictureFormats();
        int pictureFormat = parameters.getPictureFormat();
        parameters.setPictureFormat(0);

        List<Integer> previewFormats = parameters.getSupportedPreviewFormats();
        int previewFormat = parameters.getPreviewFormat();
        parameters.setPreviewFormat(0);

        List<Integer> previewFrameRates = parameters.getSupportedPreviewFrameRates();
        int previewFrameRate = parameters.getPreviewFrameRate();
        parameters.setPreviewFrameRate(0);

        //-------------------------Formats-----------
        List<int[]> previewFpsRange = parameters.getSupportedPreviewFpsRange();
        parameters.getPreviewFpsRange(new int[]{});
        parameters.setPreviewFpsRange(0,0);

        //-------------------------设置给相机--------------------
        camera.setParameters(parameters);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

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
        if (camera != null) {
            if (camera != null && CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    camera.setPreviewCallback(null);
                }
                camera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public CodeUtils.AnalyzeCallback getAnalyzeCallback() {
        return analyzeCallback;
    }

    public void setAnalyzeCallback(CodeUtils.AnalyzeCallback analyzeCallback) {
        this.analyzeCallback = analyzeCallback;
    }

    @Nullable
    CameraInitCallBack callBack;

    /**
     * Set callback for Camera check whether Camera init success or not.
     */
    public void setCameraInitCallBack(CameraInitCallBack callBack) {
        this.callBack = callBack;
    }



    interface CameraInitCallBack {
        /**
         * Callback for Camera init result.
         * @param e If is's null,means success.otherwise Camera init failed with the Exception.
         */
        void callBack(Exception e);
    }

    //menu


}
