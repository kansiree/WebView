package com.example.print.webview.knox;

import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.kioskmode.KioskMode;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by print on 11/9/2017.
 */

public class LicenseReceiver extends BroadcastReceiver {
    public static final String TAG = LicenseReceiver.class.getSimpleName();
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent!=null){
            String action = intent.getAction();

            if(action != null){
//                System.out.println("print: onReceive action "+action);
                return;
            }
            else if(action.equals(KioskMode.ACTION_ENABLE_KIOSK_MODE_RESULT)){
                System.out.println("print: onReceive action1");
                addApplicationShortcut(context);
            }
            else if (action.equals(KioskMode.ACTION_DISABLE_KIOSK_MODE_RESULT)){
                System.out.println("print: onReceive action2");
                removeApplicationShortcut(context);
            }
            else if(action.equals(KioskMode.ACTION_UNEXPECTED_KIOSK_BEHAVIOR)){
                System.out.println("print: onReceive action3");
//                SAUIHelper.showToast(context, context.getString(R.string.error_kiosk_mode));
            }
        }


    }
    void addApplicationShortcut(Context context) {
        EnterpriseDeviceManager enterpriseDeviceManager = (EnterpriseDeviceManager) context
                .getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);

        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        boolean success = appPolicy.addHomeShortcut(context.getPackageName(), null);
        Log.i(TAG, "Kiosk Sample App shortcut was added: " + success);

        // success = appPolicy.addHomeShortcut("com.android.settings", null);
        // Log.i(TAG, "Settings shortcut was added: " + success);
    }

    void removeApplicationShortcut(Context context) {
        EnterpriseDeviceManager enterpriseDeviceManager = (EnterpriseDeviceManager) context
                .getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);

        ApplicationPolicy appPolicy = enterpriseDeviceManager.getApplicationPolicy();
        boolean success = appPolicy.deleteHomeShortcut(context.getPackageName(), null);
        Log.i(TAG, "Kiosk Sample App shortcut was removed: " + success);

        success = appPolicy.deleteHomeShortcut("com.android.settings", null);
//        Log.i(TAG, "Settings shortcut was removed: " + success);
    }

}
