package com.example.print.webview.knox;

import android.app.admin.DevicePolicyManager;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.RestrictionPolicy;
import android.app.enterprise.kioskmode.KioskMode;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by print on 11/9/2017.
 */

public class SettingKnox {
    private Context context;
    private DevicePolicyManager mDPM;
    private EnterpriseDeviceManager mEDM;
    private ComponentName mDeviceAdmin;
    private RestrictionPolicy rp;

    KioskMode kioskMode;
    String TAG = "Test";
    List<Integer> listHwKey = null;


    public SettingKnox(Context context) {
        this.context = context;
    }

    public void start(){
            ActivateLicense(false);
        }

    public void stop(){
        ActivateLicense(true);
        mEDM = new EnterpriseDeviceManager(context);
        mEDM.getApplicationPolicy().stopApp("com.example.print.webview");

    }
    private void ActivateLicense( Boolean aBoolean){
        Toast toast = Toast.makeText(context,"", Toast.LENGTH_LONG);

        mDeviceAdmin = new ComponentName(context,Receiver.class);
        mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mEDM = new EnterpriseDeviceManager(context);

        try {
            kioskMode = KioskMode.getInstance(context);
            rp = mEDM.getRestrictionPolicy();
//            kioskMode.allowTaskManager(true);
//            kioskMode.hideNavigationBar(true);

            if(!kioskMode.isKioskModeEnabled()){
                rp.setHomeKeyState(aBoolean);
                listHwKey = new ArrayList<Integer>();
                listHwKey.add(KeyEvent.KEYCODE_BACK);
                listHwKey.add(KeyEvent.KEYCODE_APP_SWITCH);
                listHwKey.add(KeyEvent.KEYCODE_VOLUME_UP);
                listHwKey.add(KeyEvent.KEYCODE_VOLUME_DOWN);
                listHwKey.add(KeyEvent.KEYCODE_POWER);
                listHwKey.add(KeyEvent.FLAG_SOFT_KEYBOARD);
                listHwKey.add(KeyEvent.KEYCODE_MENU);
                kioskMode.allowHardwareKeys(listHwKey,aBoolean);
                kioskMode.allowMultiWindowMode(aBoolean);
                kioskMode.hideStatusBar(!aBoolean);
            }
            if(!aBoolean){
                toast.setText("Disable");
            }else {
                toast.setText("Enable");
            }

            toast.show();
        } catch (Exception e) {
            Log.w(TAG, "Exception: " + e);
            toast.setText("Error: Exception occurred - " + e);
            toast.show();
        }

    }

}
