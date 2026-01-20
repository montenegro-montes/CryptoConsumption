package es.uma.consumption.Thread;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import es.uma.consumption.Aplicacion.ConsumptionApplication;
import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Logger;
import es.uma.consumption.Util.Profiler;
import es.uma.consumption.Util.Store;



public class SymmetricThread extends AsyncTask<Void, String, Void> {

    final int ENCRYPT_MODE    = 0;
    final int DECRYPT_MODE    = 1;
    final int BOTH_MODE       = 2;


    private final int SLEEP_TIME_PROFILE_INIT               = 2000;
    private final int SLEEP_TIME_PROFILE_END                = 4000;
    private final int SLEEP_TIME_PROFILE_BETWEEN_SERVICES   = 1000;

    int               OperationSelected = 0;
    String            algS      = null;
    Profiler          profiler  = null;
    int               sizeText  = 0;

    Button            SimmetrycButtonBatchMode  =null;
    TextView          Progress                  =null;

    String            algthmSymMode[];
    String            algthmSymPadding[];

    Map<String, SecretKey> mapSymmKeys     = new HashMap<String, SecretKey>();

    long        timeStamp;
    byte []     datosCifrar;

    Logger loggerException, loggerTime;



    public SymmetricThread(String algSelectedP,   int operationSelectedP, int sizeP,ConsumptionApplication application) {

        timeStamp           = System.currentTimeMillis();
        sizeText             = sizeP;
        algS                = algSelectedP;
        algthmSymMode       = AlgthCfg.getAlgSymmetricMode(algS);    // CALCULO EN MAIN VUELVO CALCULAR PARA PASAR MENOS PARAMETROS
        algthmSymPadding    = AlgthCfg.getAlgSymmetricPadding(algS);

        mapSymmKeys         = application.generateKeys.getSymmetricKeys();
        profiler            = application.profiler;

        datosCifrar         = application.getData(sizeText); //96

        loggerException     = new Logger(algS+"_"+sizeText+"_"+timeStamp,false);
        loggerTime          = new Logger(algS+"_"+sizeText+"_"+timeStamp,true);

        loggerTime.addPhoneInfo(application.infoPhone); //ADD PhoneInfo

        OperationSelected   = operationSelectedP;
    }

    /************************************************************************
     *
     * @param views
     */

     public void setVisual (View... views){
        SimmetrycButtonBatchMode = (Button)   views[0];
        Progress                 = (TextView) views[1];
    }


    /************************************************************************
     *
     * @param voids
     * @return
     */


     protected Void doInBackground(Void... voids) {

        int numMode     =   algthmSymMode.length;
        int numPadding  =   algthmSymPadding.length;

        int cont = 1,i=0,j=0;

            for (i = 0; i < numMode; i++) {
                for (j = 0; j < numPadding; j++) {
                    key_for( i, j,  cont);
                }
            }





        return null;
    }


    private void key_for(int i,int j, int cont){
        int[] keysSize = AlgthCfg.SymmetrickeySize(algS,"SC");

        for (int k = 0; k < keysSize.length; k++) {
            int keySize = keysSize[k];


            publishProgress( String.valueOf(OperationSelected),algS, algthmSymMode[i], algthmSymPadding[j],String.valueOf(keySize),String.valueOf(cont));

            try {
                profilling(algS, algthmSymMode[i], algthmSymPadding[j], keySize,sizeText);
            } catch (Exception e) {
                String mensaje=e.getMessage();

                if (mensaje!=null) Log.d("ERRR",e.getMessage());
                else e.printStackTrace();

                String filename=algS + "_" + keySize + "_" + algthmSymMode[i] + "_" + algthmSymPadding[j]+"_"+timeStamp;
                loggerTime.addTime(filename+";-");
                loggerException.addHeaderToLog(algS+"_"+ algthmSymMode[i]+"_"+ algthmSymPadding[j]+"_"+keySize);
                loggerException.addExceptionToLog(e.fillInStackTrace());
            }


            cont++;
        }
    }
    /************************************************************************
     *
     * @param progress
     */


     protected void onProgressUpdate(String... progress) {


         int operation  = Integer.parseInt(progress[0]);
         String message = null;


         message = new String(progress[1]+" "+progress[5]+"\n\t"+progress[2]+" "+progress[3]+" "+progress[4]);


        if (Progress!=null) Progress.setText(message);

    }

    /************************************************************************
     *
     * @param voids
     */

     protected void onPostExecute(Void voids) {
         if (SimmetrycButtonBatchMode!=null) SimmetrycButtonBatchMode.setEnabled(true);
         if (Progress!=null)                 Progress.setText("Finished !!!");

         loggerException.doFinal();
         Log.d("SY","Finished !!!");
    }


    /************************************************************************
     *
     * @param algS
     * @param mode
     * @param Padding
     * @param keySize
     * @throws Exception
     */

    private void profilling (String algS, String mode,String Padding,int keySize,int size) throws Exception {


        String filename = algS + "_" + keySize + "_" + mode + "_" + Padding  + "_" + timeStamp;

        switch (OperationSelected) {
            case ENCRYPT_MODE:
                filename = "E_" + algS + "_" + keySize + "_" + mode + "_"  +size+"_"+ Padding + "_" + timeStamp;
                break;
            case DECRYPT_MODE:
                filename = "D_" + algS + "_" + keySize + "_" + mode + "_" + +size+"_"+ Padding  + "_" + timeStamp;
                break;
            case BOTH_MODE:
                filename = "B_" + algS + "_" + keySize + "_" + mode + "_" + Padding  + "_" + timeStamp;
                break;

        }


        String AlgoritmMode     =   null;
        SecretKey secretKey     =   null;
        Cipher cipher           =   null;
        byte[] encrypted_text   =   null;


            AlgoritmMode = algS + "/" + mode + "/" + Padding;
            secretKey    = mapSymmKeys.get(algS + keySize);
            cipher       = Cipher.getInstance(AlgoritmMode);



        encrypted_text = Store.getEncrpytedText("SC", AlgoritmMode, keySize,size);


     /*   if (cipher!=null) Log.d("CIPHER",cipher.getAlgorithm());
        else    Log.d("CIPHER","CIPHER NULL");

        if (secretKey!=null) Log.d("secretKey",secretKey.getAlgorithm());
        else    Log.d("secretKey","secretKey NULL");

        if (encrypted_text!=null) Log.d("encrypted_text ",encrypted_text.toString());
        else    Log.d("encrypted_text","encrypted_text NULL");*/

        Log.d("PRUEBA","START PROFILLING "+filename);

        long iniTime = System.currentTimeMillis();
        profiler.startProfilling(filename);

                SystemClock.sleep(SLEEP_TIME_PROFILE_INIT);         // wait milliseconds

                switch (OperationSelected){
                    case ENCRYPT_MODE: encrypted_text = Encrypt     (algS,cipher,mode,secretKey,datosCifrar);  break;
                    case DECRYPT_MODE:                  Decrypt     (algS,cipher,mode,secretKey,encrypted_text);  break;
                    case BOTH_MODE:                     Both        (cipher,mode,secretKey,datosCifrar);  break;
                }

                SystemClock.sleep(SLEEP_TIME_PROFILE_END);          // wait milliseconds

        profiler.stopProfilling();
        long finTime = System.currentTimeMillis();

        long duration = (finTime-iniTime-SLEEP_TIME_PROFILE_INIT-SLEEP_TIME_PROFILE_END);
        Log.d("PRUEBA", "END PROFILLING " + filename+" "+ duration +" milliseconds.");

        loggerTime.addTime(filename+";"+ duration);


        if (OperationSelected ==ENCRYPT_MODE) Store.setEncrpytedText("SC",AlgoritmMode,keySize,encrypted_text,size);


        SystemClock.sleep(SLEEP_TIME_PROFILE_BETWEEN_SERVICES);

      }

    /************************************************************************
     *

     */

    private byte [] Encrypt(String alg,Cipher cipher,String mode,  SecretKey secretKey,byte []data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {

            byte [] encrypted_text  = null;



            if (mode.contentEquals("ECB")){

                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                encrypted_text = cipher.doFinal(data);
            }
            else {
                String initVector = IV(alg);
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));

                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                encrypted_text = cipher.doFinal(data);

            }

            return encrypted_text;

    }


    private byte [] Encrypt2(  ) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException {

        byte [] encrypted_text  = null;

        byte []data = new byte[100];

        SecretKey secretKey;
        KeyGenerator keyGen = null;

        keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        secretKey = keyGen.generateKey();


        Cipher cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        encrypted_text = cipher.doFinal(data);


        return encrypted_text;

    }


    /************************************************************************
     *
     *
     *
     */

    private void Decrypt(String alg,Cipher cipher,String mode,  SecretKey secretKey,byte [] encrypted_text) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        byte [] decrypted_text;

            if (mode.contentEquals("ECB")){

                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                decrypted_text=cipher.doFinal(encrypted_text);
            }
            else {
                String initVector = IV(alg);
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));

                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                decrypted_text=cipher.doFinal(encrypted_text);
            }

        //boolean equal = java.util.Arrays.equals(decrypted_text, datos);
        //if (!equal) Log.d("INFO ","Error Decrypt");
        //else        Log.d("INFO ","Decrypt Correcto");
      }

    /************************************************************************
     *
     *
     */


    private void Both(Cipher cipher,String mode,  SecretKey secretKey,byte [] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException {


        byte [] encrypted_text  = null;
        byte [] decrypted_text  = null;

           if (mode.contentEquals("ECB")){

                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                encrypted_text = cipher.doFinal(data);

                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                decrypted_text = cipher.doFinal(encrypted_text);
            }
            else {
                String initVector = IV(algS);
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));

                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                encrypted_text = cipher.doFinal(data);

                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                decrypted_text = cipher.doFinal(encrypted_text);
            }




        //boolean equal = java.util.Arrays.equals(decrypted_text, data);
        //if (!equal) Log.d("INFO ","Error Decrypt");
    }


    /************************************************************************
     *
     * @param algS
     * @return
     */

    private String IV (String algS){
        String initVector="RandomIn";

        if (algS.contentEquals("AES")){
            initVector             = "RandomInRandomIn";
        }

        return initVector;
    }




}
