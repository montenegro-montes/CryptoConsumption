package es.uma.consumption.Util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by joseamontenegromontes on 10/12/16.
 */

public class Profiler {

    Context _currentActivity;


    public Profiler(Context currentActivity){
        _currentActivity = currentActivity;

      /*  PackageManager manager = _currentActivity.getPackageManager();

        Intent startProfiling = new Intent("com.quicinc.trepn.start_profiling");

        List<ResolveInfo> infos = manager.queryIntentActivities(startProfiling, 0);

        if (infos.size() <= 0) {
            Log.d("ERROR","Profiler no instalado");
        }*/
    }





    public void runProfillingService (){
       /* Intent trepnProfiler = new Intent();
        //adb shell am startservice com.quicinc.trepn/.TrepnService
        trepnProfiler.setClassName("com.quicinc.trepn", "com.quicinc.trepn.TrepnService");
        _currentActivity.startService(trepnProfiler);
        */
   }

    //adb shell am broadcast –a com.quicinc.trepn.start_profiling
    //adb shell am broadcast –a com.quicinc.trepn.start_profiling
    //                       –e com.quicinc.trepn.database_file “<string_value>”
    public void startProfilling (String database){
        /*Intent startProfiling = new Intent("com.quicinc.trepn.start_profiling");
        if (database!=null) startProfiling.putExtra("com.quicinc.trepn.database_file",database);
        _currentActivity.sendBroadcast(startProfiling);*/

       /* Intent tutorProfiler = new Intent();
        tutorProfiler.setClassName("edu.umich.PowerTutor", "edu.umich.PowerTutor.service.UMLoggerService");
        _currentActivity.startService(tutorProfiler);*/

        Intent startProfiling = new Intent("edu.umich.PowerTutor.start_profiling");
        if (database!=null) startProfiling.putExtra("filename",database);
        _currentActivity.sendBroadcast(startProfiling);
    }

    public void stopProfilling (){
        /*Intent stopProfiling = new Intent("com.quicinc.trepn.stop_profiling");
        _currentActivity.sendBroadcast(stopProfiling);*/

       /* Intent tutorProfiler = new Intent();
        tutorProfiler.setClassName("edu.umich.PowerTutor", "edu.umich.PowerTutor.service.UMLoggerService");
        _currentActivity.stopService(tutorProfiler);*/

        Intent stateUpdate = new Intent("edu.umich.PowerTutor.stop_profiling");
        _currentActivity.sendBroadcast(stateUpdate);
    }

    public void newState (int stateInt, String value){

        Intent stateUpdate = new Intent("com.quicinc.Trepn.UpdateAppState");
        stateUpdate.putExtra("com.quicinc.Trepn.UpdateAppState.Value", stateInt);
        stateUpdate.putExtra("com.quicinc.Trepn.UpdateAppState.Value.Desc",value);
        _currentActivity.sendBroadcast(stateUpdate);
    }

    public void loadPreferences (String file){
        Intent loadPreferences = new Intent("com.quicinc.trepn.load_preferences");
        loadPreferences.putExtra("com.quicinc.trepn.load_preferences_file", file);
        _currentActivity.sendBroadcast(loadPreferences);
    }

    public void savePreferences (String file){
        Intent savePreferences = new Intent("com.quicinc.trepn.save_preferences");
        savePreferences.putExtra("com.quicinc.trepn.save_preferences_file", file);
        _currentActivity.sendBroadcast(savePreferences);
    }


    //adb shell am broadcast -a com.quicinc.trepn.export_to_csv
    //                      -e com.quicinc.trepn.export_db_input_file “<existing_database_name>”
    //                      -e com.quicinc.trepn.export_csv_output_file “<output_csv_file>”

    public void convertcsv(String file){
       /* Intent startProfiling = new Intent("com.quicinc.trepn.export_to_csv");
        startProfiling.putExtra("com.quicinc.trepn.export_db_input_file",file);
        _currentActivity.sendBroadcast(startProfiling);*/
    }
}
