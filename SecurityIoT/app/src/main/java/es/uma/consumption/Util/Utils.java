package es.uma.consumption.Util;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by joseamontenegromontes on 8/12/16.
 */

public class Utils {

    public static String toHex(byte[] bytes){

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }


    public static String readRawTextFileString(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return text.toString();
    }

    public static byte[] readRawTextFileByte(Context ctx, int resId) {
        return readRawTextFileString(ctx, resId).getBytes();
    }

    public static String phoneInformation(Context context){
        String manufacturer = Build.MANUFACTURER;
        String device       = Build.DEVICE;
        String brand        = Build.BRAND;
        String product      = Build.PRODUCT;
        String model        = Build.MODEL;
        int sdk             = Build.VERSION.SDK_INT;
        String os           = System.getProperty("os.version");
        String osVersion    = Build.VERSION.INCREMENTAL;
        String apiLevel     = Build.VERSION.RELEASE;
        String sno          = Build.SERIAL;


        initAirplanemodeBtn(context);

        ActivityManager actvityManager = (ActivityManager) context.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();

        /*String process;
        int uid;

        for(ActivityManager.RunningAppProcessInfo runningProInfo:procInfos){

            process =  runningProInfo.processName;
            uid     =    runningProInfo.uid;

            Log.d("Running Processes", "("+uid+")"+process);
        }*/

        int numProcess =   procInfos.size();
        int myuid = android.os.Process.myUid();

        boolean isEnabledAIRPLANE = Settings.System.getInt(
                context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;

        String phoneInfo="";
        phoneInfo += "\n% Device (brand): " + manufacturer+" "+device+ " ("+ brand + ")";
        phoneInfo += " Serial number: " + sno;
        phoneInfo += "\n% OS API Level: "+ apiLevel + " ("+ sdk +")";
        phoneInfo += "\n% OS Version: " + os + " (" + osVersion + ")";
        phoneInfo += "\n% Model (and Product): " + model + " ("+ product + ")";
        phoneInfo += "\n% UID: " + myuid+" Num. process: "+numProcess+" AIRPLANE enabled: "+isEnabledAIRPLANE;

        Log.d("INFO phoneInfo",phoneInfo);



        return phoneInfo;
    }

    @SuppressWarnings("deprecation")
    private static void initAirplanemodeBtn(Context context) {

        boolean isEnabled = Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1;

        if (!isEnabled) {

            if (android.os.Build.VERSION.SDK_INT < 17) {
                try {
                    // switch on airplane mode
                    Settings.System.putInt(
                            context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, 1);

                    // Post an intent to reload
                    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    intent.putExtra("state", !isEnabled);
                    context.sendBroadcast(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }

        ContentResolver contentResolver= context.getContentResolver();

        Settings.System.putInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);


        int bright=1;
        android.provider.Settings.System.putInt(contentResolver,
                android.provider.Settings.System.SCREEN_BRIGHTNESS, bright);
    }


}
