package es.uma.consumption.Thread;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import es.uma.consumption.Aplicacion.ConsumptionApplication;
import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Logger;
import es.uma.consumption.Util.Profiler;


public class CommThread extends AsyncTask<Void, String, Void> {

    final int WIFI_MODE        = 0;
    final int BLUE_MODE        = 1;

    private final int SLEEP_TIME_PROFILE_INIT               = 2000;
    private final int SLEEP_TIME_PROFILE_END                = 4000;
    private final int SLEEP_TIME_PROFILE_BETWEEN_SERVICES   = 1000;

    int         OperationSelected       = 0;
    Profiler    profiler                = null;

    Button      MACButtonBatchMode =null;
    TextView    Progress                  =null;

    String ip;
    int port;
    int sizeText;
    long timeStamp;

    String datos;

    Logger loggerException, loggerTime;


    public CommThread(String ipP, int portP, int operationSelectedP, int sizeP,ConsumptionApplication application) {

        ip=ipP;
        port=portP;
        sizeText = sizeP;
        timeStamp           = System.currentTimeMillis();

        profiler            = application.profiler;
        OperationSelected   = operationSelectedP;
        datos               = application.getDataString(sizeText);


        String Ope          = null;
        switch (OperationSelected){

            case WIFI_MODE:         Ope="WIFI";         break;
            case BLUE_MODE:         Ope="BLUE";          break;
        }

        loggerException     = new Logger(Ope+"_"+sizeText+"_"+timeStamp,false);
        loggerTime          = new Logger(Ope+"_"+sizeText+"_"+timeStamp,true);

        loggerTime.addPhoneInfo(application.infoPhone); //ADD PhoneInfo

        OperationSelected   = operationSelectedP;

    }
    /************************************************************************
     *
     */

    public void setVisual (View... views){
        MACButtonBatchMode      = (Button)   views[0];
        Progress                 = (TextView) views[1];
    }


    /************************************************************************
     *
     * @param voids
     * @return
     */


     protected Void doInBackground(Void... voids) {



             publishProgress( String.valueOf(OperationSelected));


             try {
                 profilling( );

             } catch (Exception e) {

                 String mensaje=e.getMessage();

                 if (mensaje!=null) Log.d("ERRR",e.getMessage());
                 else               e.printStackTrace();

                 String filename= "_O_"+OperationSelected+"_S_"+sizeText+"_"+timeStamp;
                 loggerTime.addTime(filename+";-");
                 loggerException.addHeaderToLog("_");
                 loggerException.addExceptionToLog(e.fillInStackTrace());
             }




        return null;
    }


    /************************************************************************
     *
     * @param progress
     */


     protected void onProgressUpdate(String... progress) {
         int operation  = Integer.parseInt(progress[0]);
         String message = null;

        //message = new String (progress[1]+" "+progress[2]);
         message = new String (progress[0]);

         if (Progress!=null) Progress.setText(message);

    }

    /************************************************************************
     *
     * @param voids
     */

     protected void onPostExecute(Void voids) {
         if(MACButtonBatchMode !=null) MACButtonBatchMode.setEnabled(true);
         if (Progress!=null) Progress.setText("Finished !!!");

         Log.d("ASY","Finished !!!");
         loggerException.doFinal();
    }


    /************************************************************************
     *
     * @throws Exception
     */

    private void profilling () throws Exception {


        String filename =  "_S_"+sizeText+"_"+timeStamp;

        switch (OperationSelected){
            case WIFI_MODE:         filename = "W" + "_S_"+sizeText+"_"+timeStamp;  break;
            case BLUE_MODE:         filename = "B" + "_S_"+sizeText+"_"+timeStamp;  break;

        }

        MessageDigest md    =null;
        Mac mac             =null;
        SecretKey key       =null;
        int length          =datos.length();

        Log.d("PRUEBA","START PROFILLING "+filename+" length "+length);

        long iniTime = System.currentTimeMillis();
        profiler.startProfilling(filename);

                SystemClock.sleep(SLEEP_TIME_PROFILE_INIT);         // wait milliseconds

                switch (OperationSelected){
                    case WIFI_MODE:         sendWifiData        (ip,port,datos);  break;
                }

                SystemClock.sleep(SLEEP_TIME_PROFILE_END);          // wait milliseconds

        profiler.stopProfilling();
        long finTime = System.currentTimeMillis();

        long duration = (finTime-iniTime-SLEEP_TIME_PROFILE_INIT-SLEEP_TIME_PROFILE_END);
        Log.d("PRUEBA", "END PROFILLING " + filename+" "+ duration +" milliseconds.");

        loggerTime.addTime(filename+";"+ duration);

        SystemClock.sleep(SLEEP_TIME_PROFILE_BETWEEN_SERVICES);

      }





    private void sendWifiData(String ip,int puerto,String mensaje) throws IOException {

            Socket sk = new Socket(ip,puerto);

            PrintWriter salida = new PrintWriter(
                    new OutputStreamWriter(sk.getOutputStream()),true);
            salida.println(mensaje);
            salida.close();
            //sk.close();

    }
}
