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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import es.uma.consumption.Aplicacion.ConsumptionApplication;
import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Logger;
import es.uma.consumption.Util.Profiler;
import es.uma.consumption.Util.Store;


public class AsymmetricThread extends AsyncTask<Void, String, Void> {

    final int ENCRYPT_MODE    = 0;
    final int DECRYPT_MODE    = 1;
    final int ED_MODE         = 2;
    final int SIGN_MODE       = 5;
    final int VERIFY_MODE     = 6;
    final int SV_MODE         = 7;



    private final int SLEEP_TIME_PROFILE_INIT               = 2000;
    private final int SLEEP_TIME_PROFILE_END                = 4000;
    private final int SLEEP_TIME_PROFILE_BETWEEN_SERVICES   = 1000;

    int         OperationSelected       = 0;
    String      algS                    = null;
    Profiler    profiler                = null;

    Button      SimmetrycButtonBatchMode  =null;
    TextView    Progress                  =null;

    String      algthmAsymPadding[];
    String      algthmAsymHash[];
    String      algthAsyMode[];

    int               sizeText  = 0;

    Map<String, KeyPair> mapAsymKeys = new HashMap<String, KeyPair>();

    long timeStamp;

    byte [] datosCifrar;

    Logger loggerException, loggerTime;


    public AsymmetricThread(String algSelectedP,  int operationSelectedP, int sizeP, ConsumptionApplication application) {

        timeStamp           = System.currentTimeMillis();
        sizeText             = sizeP;

        algS                = algSelectedP;
        algthmAsymPadding   = AlgthCfg.getAlgAsymmetricPadding(algS);
        algthmAsymHash      = AlgthCfg.getAlgAsymmetricHash(algS);
        mapAsymKeys         = application.generateKeys.getAsymmetricKeys();
        algthAsyMode        = AlgthCfg.getAlgAsymmetricMode(algS);
        profiler            = application.profiler;

        datosCifrar         = application.getData(sizeText); //

        loggerException     = new Logger(algS+"_"+sizeText+"_"+timeStamp,false);
        loggerTime          = new Logger(algS+"_"+sizeText+"_"+timeStamp,true);

        loggerTime.addPhoneInfo(application.infoPhone); //ADD PhoneInfo

        OperationSelected   = operationSelectedP;

    }
    /************************************************************************
     *
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


         String [] algthmAsymPadding_HASH   = null;
         int    numPadding_Hash             = -1;
         int    numMode                     = -1;

         if (algthAsyMode!=null) numMode =algthAsyMode.length;

             algthmAsymPadding_HASH =   algthmAsymPadding;
             numPadding_Hash        =   algthmAsymPadding.length;

            int cont                    = 1;
            int cont_numPadding_Hash    = 0;
            int cont_numMode            = 0;


               for (cont_numMode=0; cont_numMode < numMode;cont_numMode++) {
                   for (cont_numPadding_Hash = 0; cont_numPadding_Hash < numPadding_Hash; cont_numPadding_Hash++) {
                       key_for(cont_numPadding_Hash, cont, algthmAsymPadding_HASH,algthAsyMode[cont_numMode]);
                   }
               }


        return null;
    }

    /**********************************************************************
     * Esta ruta está fuera para poder crear las claves, ya que estas no depeden ni del padding ni del Hash.
     * @param j
     * @param cont
     * @param algthmAsymPadding_HASH
     */

        private void key_for (int j,int cont,String [] algthmAsymPadding_HASH,String Mode ){
            int[] keysSize = AlgthCfg.AsymmetrickeySize(algS,"SC");

            for (int k = 0; k < keysSize.length; k++) {
                int keySize = keysSize[k];


                publishProgress( String.valueOf(OperationSelected),algS, algthmAsymPadding_HASH[j],String.valueOf(keySize),String.valueOf(cont),Mode);


                try {
                    profilling(algS, algthmAsymPadding_HASH[j],keySize,Mode,sizeText);

                } catch (Exception e) {

                    String mensaje=e.getMessage();

                    if (mensaje!=null) Log.d("ERRR",e.getMessage());
                    else e.printStackTrace();

                    String filename=algS + "_" + keySize + "_" + algthmAsymPadding_HASH[j]+"_"+timeStamp;
                    loggerTime.addTime(filename+";-");
                    loggerException.addHeaderToLog(algS+"_"+ algthmAsymPadding_HASH[j]+"_"+keySize);
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

         String Mode = progress[5]; //if Mode!=null


         switch(operation){
             case ENCRYPT_MODE:
             case DECRYPT_MODE:
             case ED_MODE: message = new String(progress[1] + " " + progress[4] + " " + progress[5]+
                                                "\n\t" + progress[2] + " " + progress[3]);
                            break;
         }

         if (Progress!=null) Progress.setText(message);

    }

    /************************************************************************
     *
     * @param voids
     */

     protected void onPostExecute(Void voids) {
         if(SimmetrycButtonBatchMode!=null) SimmetrycButtonBatchMode.setEnabled(true);
         if (Progress!=null) Progress.setText("Finished !!!");

         Log.d("ASY","Finished !!!");
         loggerException.doFinal();
    }


    /************************************************************************
     *
     * @param algS
     * @param Padding_Hash
     * @param keySize
     * @throws Exception
     */

    private void profilling(String algS, String Padding_Hash, int keySize, String Mode, int size) throws Exception {


        String filename=algS + "_" + keySize + "_"  + Padding_Hash+"_"+timeStamp;

        switch (OperationSelected){
            case ENCRYPT_MODE:      filename = "E_" +algS + "_" + keySize + "_" + Mode + "_" +size+"_"+ Padding_Hash + "_"+timeStamp;  break;
            case DECRYPT_MODE:      filename = "D_" +algS + "_" + keySize + "_" + Mode + "_" +size+"_"+ Padding_Hash + "_"+timeStamp;  break;
            case ED_MODE:           filename = "ED_"+algS + "_" + keySize + "_" + Mode + "_" +size+"_"+ Padding_Hash + "_"+timeStamp;  break;
              }

        String AlgoritmMode     =null;
        Cipher cipher           =null;
        Signature signature     =null;
        byte[] encrypted_text   =null,signatureResult=null;
        KeyPair KeyS            =null;
        int length              =-1;

        KeyS =  mapAsymKeys.get(algS + keySize);

        length           = datosCifrar.length;
        int keySizeOct   = keySize/8;

        switch (OperationSelected){
            case  ENCRYPT_MODE:
            case  DECRYPT_MODE:
            case  ED_MODE:
                            if (Padding_Hash.contains("NoPadding")){
                                length = keySizeOct-11;
                            }
                            else if (Padding_Hash.contains("PKCS1Padding")){
                                length = keySizeOct-11;
                            }
                            else if (Padding_Hash.contains("OAEP")){

                                if (Padding_Hash.contains("SHA-1")) {
                                    length = keySizeOct- (2*20)-2;
                                }
                                else   if (Padding_Hash.contains("SHA-256")){
                                    length = keySizeOct- (2*32)-2;
                                }
                                else{
                                    length = keySizeOct- (2*20)-2;
                                }
                            }

                            AlgoritmMode    = algS + "/"+Mode+"/" + Padding_Hash;
                            cipher          = Cipher.getInstance(AlgoritmMode);
                            encrypted_text  = Store.getEncrpytedText("SC", AlgoritmMode, keySize,size);
                                break;

        }

        if (length<0)                       length=0;
        else if (length>datosCifrar.length) length=datosCifrar.length;


        Log.d("PRUEBA","START PROFILLING "+filename+" length "+length);

        long iniTime = System.currentTimeMillis();
        profiler.startProfilling(filename);

                SystemClock.sleep(SLEEP_TIME_PROFILE_INIT);         // wait milliseconds

                switch (OperationSelected){
                    case ENCRYPT_MODE: encrypted_text = Encrypt     (cipher,KeyS.getPublic(),datosCifrar,length);  break;
                    case DECRYPT_MODE:       if(encrypted_text!=null)   Decrypt     (cipher,KeyS.getPrivate(),encrypted_text);  break;
                    case ED_MODE:                       E_D         (cipher,KeyS.getPublic(),KeyS.getPrivate(),datosCifrar,length);  break;
                }

                SystemClock.sleep(SLEEP_TIME_PROFILE_END);          // wait milliseconds

        profiler.stopProfilling();
        long finTime = System.currentTimeMillis();

        long duration = (finTime-iniTime-SLEEP_TIME_PROFILE_INIT-SLEEP_TIME_PROFILE_END);
        Log.d("PRUEBA", "END PROFILLING " + filename+" "+ duration +" milliseconds.");

        loggerTime.addTime(filename+";"+ duration);


        switch (OperationSelected){
            case ENCRYPT_MODE: Store.setEncrpytedText("SC",AlgoritmMode,keySize,encrypted_text,size); break;
        }

        SystemClock.sleep(SLEEP_TIME_PROFILE_BETWEEN_SERVICES);

      }



    /************************************************************************
     *
     * @param cipher
     * @param key
     * @param data
     * @returnñ
     * @throws Exception
     */

    private byte[] Encrypt(Cipher cipher,PublicKey key,byte []data,int length) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        byte[] cipherText = null;


            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(data,0,length);


        return cipherText;
    }
    /************************************************************************
     *
     *
     *
     */

    private void Decrypt(Cipher cipher,PrivateKey key, byte [] encrypted_text) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        byte [] decrypted_text;

                cipher.init(Cipher.DECRYPT_MODE, key);
                decrypted_text=cipher.doFinal(encrypted_text);

        //boolean equal = java.util.Arrays.equals(decrypted_text, datos);
        //if (!equal) Log.d("INFO ","Error Decrypt");
        //else        Log.d("INFO ","Decrypt Correcto");
      }

    /************************************************************************
     *
     *
     */


    private void E_D (Cipher cipher, PublicKey keyPu, PrivateKey keyPr,byte [] data,int length) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException {


        byte [] encrypted_text  = null;
        byte [] decrypted_text  = null;


                cipher.init(Cipher.ENCRYPT_MODE, keyPu);
                encrypted_text  = cipher.doFinal(data,0,length);

                cipher.init(Cipher.DECRYPT_MODE, keyPr);
                decrypted_text = cipher.doFinal(encrypted_text);
    }









}
