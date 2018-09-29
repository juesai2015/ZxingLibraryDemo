package com.uuzuche.lib_zxing;

import android.content.Context;
import android.hardware.Camera;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;

/**
 * Created by user on 2017/7/31.
 */

public class ScanTools {

    private  Camera.Parameters parameters;
    private  Camera camera;
    private  Context context;
    private static ScanTools INSTANCE;

    private ScanTools(Camera camera,Camera.Parameters parameters, Context context){
        this.camera = camera;
        this.parameters = parameters;
        this.context = context;
    }

    public static ScanTools newInstance(Camera camera,Camera.Parameters parameters, Context context){
        INSTANCE = new ScanTools(camera,parameters, context);
        return INSTANCE;
    }


    public void changeColor(final Button btn){
        PopupMenu pop = new PopupMenu(context, btn);
        pop.getMenuInflater().inflate(R.menu.menu, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.effect_none) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                }
                else if (i == R.id.effect_mono) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                }
                else if (i == R.id.effect_negative) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                }
                else if (i == R.id.effect_solarize) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                }
                else if (i == R.id.effect_sepia) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                }
                else if (i == R.id.effect_posterize) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                }
                else if (i == R.id.effect_whiteboard) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
                }
                else if (i == R.id.effect_blackboard) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
                }
                else if (i == R.id.effect_aqua) {
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                }
                btn.setText(item.getTitle());
                camera.setParameters(parameters);
                return false;

            }

        });

    }
    public void changeFocus(final Button btn){
        PopupMenu pop = new PopupMenu(context, btn);
        pop.getMenuInflater().inflate(R.menu.menu_focus, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.focus_mode_auto) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                else if (i == R.id.focus_mode_infinity) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                }
                else if (i == R.id.focus_mode_macro) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                }
                else if (i == R.id.focus_mode_fixed) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
                }
                else if (i == R.id.focus_mode_edof) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_EDOF);
                }
                else if (i == R.id.focus_mode_continuous_video) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                else if (i == R.id.focus_mode_continuous_picture) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                btn.setText(item.getTitle());
                camera.setParameters(parameters);
                return false;
            }
        });
    }
    public void changeFlash(final Button btn){
        PopupMenu pop = new PopupMenu(context, btn);
        pop.getMenuInflater().inflate(R.menu.menu_flash, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.flash_mode_auto) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                }
                else if (i == R.id.flash_mode_on) {
                  //  parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
                else if (i == R.id.flash_mode_off) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                else if (i == R.id.flash_mode_red_eye) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
                }
                else if (i == R.id.flash_mode_torch) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
                btn.setText(item.getTitle());
                camera.setParameters(parameters);
                return false;
            }
        });
    }
    public void changeWhite(final Button btn){
        PopupMenu pop = new PopupMenu(context, btn);
        pop.getMenuInflater().inflate(R.menu.menu_white, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.white_balance_auto) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                }
                else if (i == R.id.white_balance_cloudy_daylight) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
                }
                else if (i == R.id.white_balance_daylight) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_DAYLIGHT);
                }
                else if (i == R.id.white_balance_fluorescent) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
                }
                else if (i == R.id.white_balance_incandescent) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_INCANDESCENT);
                }
                else if (i == R.id.white_balance_shade) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_SHADE);
                }
                else if (i == R.id.white_balance_twilight) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_TWILIGHT);
                }
                else if (i == R.id.white_balance_warm_fluorescent) {
                    parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT);
                }
                btn.setText(item.getTitle());
                camera.setParameters(parameters);
                return false;
            }
        });
    }
    public void changeZoom(final Button btn){
        PopupMenu pop = new PopupMenu(context, btn);
        pop.getMenuInflater().inflate(R.menu.menu_zoom, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.zoom20) {
                    parameters.setZoom(20);
                }
                else if (i == R.id.zoom40) {
                    parameters.setZoom(40);
                }
                else if (i == R.id.zoom60) {
                    parameters.setZoom(60);
                }
                else if (i == R.id.zoom78) {
                    parameters.setZoom(80);
                }
                else if (i == R.id.zoom2) {
                    parameters.setZoom(2);
                }
                btn.setText(item.getTitle());
                camera.setParameters(parameters);
                return false;
            }
        });
    }
    public void changeScene(final Button btn){
        PopupMenu pop = new PopupMenu(context, btn);
        pop.getMenuInflater().inflate(R.menu.menu_scene, pop.getMenu());
        pop.show();
        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.scene_mode_action) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
                }
                else if (i == R.id.scene_mode_auto) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                }
                else if (i == R.id.scene_mode_barcode) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_BARCODE);
                }
                else if (i == R.id.scene_mode_beach) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_BEACH);
                }
                else if (i == R.id.scene_mode_candlelight) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_CANDLELIGHT);
                }
                else if (i == R.id.scene_mode_fireworks) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_FIREWORKS);
                }
                else if (i == R.id.scene_mode_hdr) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
                }
                else if (i == R.id.scene_mode_landscape) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
                }
                else if (i == R.id.scene_mode_night) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
                }
                else if (i == R.id.scene_mode_night_portrait) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT);
                }
                else if (i == R.id.scene_mode_party) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
                }
                else if (i == R.id.scene_mode_portrait) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
                }
                else if (i == R.id.scene_mode_snow) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SNOW);
                }
                else if (i == R.id.scene_mode_sports) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
                }
                else if (i == R.id.scene_mode_steadyphoto) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
                }
                else if (i == R.id.scene_mode_sunset) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_SUNSET);
                }
                else if (i == R.id.scene_mode_theatre) {
                    parameters.setSceneMode(Camera.Parameters.SCENE_MODE_THEATRE);
                }
                btn.setText(item.getTitle());
                camera.setParameters(parameters);
                return false;
            }
        });
    }
}
