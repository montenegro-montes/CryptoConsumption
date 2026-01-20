package es.uma.consumption.Thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Store;

/**
 * Created by monte on 14/2/17.
 */

public class KeysThread extends AsyncTask<Void, String, Void> {

    Map<String, SecretKey> mapSymmetricKeys;
    Map<String, KeyPair>   mapAsymmetricKeys;

    Context context;

    Button [] botones;
    TextView  PanelInfo  =null;
                                // NOT FlexiCore
    String KEY_PROVIDER_DEFAULT = "SC"; // Same provider for all keys
    //String KEY_PROVIDER_FLEXICORE = "FlexiCore"; // Same provider for all keys


    public KeysThread (Context contextP, TextView PanelInfoP, Button... buttons){
        super();

        if (buttons.length>0) {
            botones = buttons;
            for (int i=0;i<botones.length;i++){
                botones[i].setEnabled(false);
            }
        }

        context     = contextP;
        PanelInfo   = PanelInfoP;

        mapSymmetricKeys  = new HashMap<String, SecretKey>();
        mapAsymmetricKeys = new HashMap<String, KeyPair>();
    }

    /******************************************************
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {

        publishProgress("Loading keys Please Wait!!!", "");

        LoadSymmetricKeys();

        publishProgress("\t "+NumSymKeys()+" Symmetric keys done 1/2", "");

        LoadAsymmetricKeys();

        publishProgress("\t "+NumAsymKeys()+" Asymmetric keys done 2/2 ", "");

        //LoadMACKeys();

        //publishProgress("\t "+NumMACKeys()+" Mac keys done 3/3", "");

        publishProgress("Keys done Thank you!!!", "");

        return null;
    }

    /******************************************************
     *
     * @param voids
     */
    protected void onPostExecute(Void voids) {
       Log.d("KEYS","FINISHED");
        for (int i=0;i<botones.length;i++){
            botones[i].setEnabled(true);
        }

    }

    /******************************************************
     *
     * @return
     */

    public Map getSymmetricKeys(){
        return mapSymmetricKeys;
    }

    /******************************************************
     *
     * @return
     */

    public Map getAsymmetricKeys(){
        return mapAsymmetricKeys;
    }




    /******************************************************
     *
     */

    private void LoadSymmetricKeys() {


        Map<String,?> keys = Store.getSymmetricKeys();

        int lengthKeySaved  = keys.size();
        int lengthKeyWanted = NumSymKeys();

        Log.d("Load Symmetric Keys", "Keys saved: " + lengthKeySaved+ " Keys wanted: "+lengthKeyWanted);

        if (lengthKeySaved==lengthKeyWanted) {


            for (Map.Entry<String, ?> entry : keys.entrySet()) {

                String key      =   entry.getKey();
                String value    =   entry.getValue().toString();
                Log.d("map values", key + ": " + value);

                byte[] secretKeyB = Base64.decode(value, Base64.DEFAULT);

                String algorithm="AES";

                if (key.contains("AES")) algorithm="AES";
               /* else if (key.contains("RC4")) algorithm="RC4";
                else if (key.contains("BLOWFISH")) algorithm="BLOWFISH";
                else if (key.contains("DESede")) algorithm="DESede";
                else if (key.contentEquals("DES64")) algorithm="DES";*/

                SecretKey secretKey = new SecretKeySpec(secretKeyB, 0, secretKeyB.length,algorithm);
                mapSymmetricKeys.put(entry.getKey(),secretKey);

            }
        }
        else
            GenerateSymKeys();

    }

    /*************
     * Generamos todas las claves posibles para reutilizar las mismas en cada sesion.
     *

     *
     */

    private void GenerateSymKeys(){


            Store.clearStoreSymmetricKey();

            for (String alg : AlgthCfg.algthmSym) {

                int[] keysSize = AlgthCfg.SymmetrickeySize(alg,getProvidertoGenerateKey(alg));

                for (int k : keysSize) {

                    KeyGenerator keyGen = null;
                    SecretKey secretKey;
                    String keyHashMap =  alg + k ;

                    try {
                        keyGen = KeyGenerator.getInstance(alg);
                        keyGen.init(k);
                        secretKey = keyGen.generateKey();


                        Log.d("OK GENERATED SY_KEY:", keyHashMap+" Provider "+keyGen.getProvider().getName());
                      //  publishProgress(keyHashMap, keyGen.getProvider().getName());

                        mapSymmetricKeys.put(keyHashMap, secretKey);
                        Store.addSymmetricKey(keyHashMap, secretKey);


                    } catch (NoSuchAlgorithmException e) {
                        Log.d("ERR GENERATED SY_KEY:", keyHashMap + " " + e.getMessage());
                        // e.printStackTrace();
                    }
                }
            }

    }


    /*****************************************************************
     * Verifica si están todas las claves generadas en las preferencias.
     * Si añado un proveedor o un algoritmo es necesario generar las claves de nuevo.
     * Problema de realizar una solución por lotes. De forma específica no tendríamos ese problema
     * pero sería necesario incluirlo al principio.
     * @return
     */

    private int NumSymKeys(){

        int claves=0;

            for (String alg : AlgthCfg.algthmSym) {
                int[] keysSize = AlgthCfg.SymmetrickeySize(alg,getProvidertoGenerateKey(alg));
                for (int k : keysSize) {
                    claves++;
                }
            }

        return claves;
    }

    private int NumMACKeys(){

        int claves=0;

        for (String alg : AlgthCfg.algorithmsMAC) {
                claves++;
        }

        return claves;//(claves*2);
    }
    /*****************************************************************
     *
     *
     *
     *
     */

    private int NumAsymKeys(){

        int claves=0;

            for (String alg : AlgthCfg.algthmASymSign) {
                int[] keysSize = AlgthCfg.AsymmetrickeySize(alg,getProvidertoGenerateKey(alg));
                for (int k : keysSize) {
                    claves++;
                }
            }

        return claves;
    }


    /*****************************************************************
     *
     */

    private void LoadAsymmetricKeys() {


        Map<String,?> keys = Store.getASymmetricKeys();

        int lengthKeySaved  = keys.size();
        int lengthKeyWanted = NumAsymKeys()*2; //OJO QUE SE ALMACENAN PRI y PU de cada clave, el !!doble!!.

        Log.d("Load Asymmetric Keys", "Keys saved: " + lengthKeySaved+ " Keys wanted: "+lengthKeyWanted);

        if (lengthKeySaved==lengthKeyWanted) {

            Map<String,String> Pubkeys  = new HashMap<String, String>();
            Map<String,String> Privkeys = new HashMap<String, String>();


            for (Map.Entry<String, ?> entry : keys.entrySet()) {

                String key      =   entry.getKey();
                String value    =   entry.getValue().toString();
                Log.d("map values", key + ": " + value);

                if (key.contains("Pu")) Pubkeys.put(key,value);
                else                    Privkeys.put(key,value);

            }
                for (String alg : AlgthCfg.algthmASymSign) {

                    int[] keysSize = AlgthCfg.AsymmetrickeySize(alg,getProvidertoGenerateKey(alg));

                    for (int k : keysSize) {

                        String keyHashMap = alg + k;

                        String valuePu = Pubkeys.get(keyHashMap + "Pu");
                        byte[] publicKeyBytes = Base64.decode(valuePu, Base64.DEFAULT);

                        String valuePr = Privkeys.get(keyHashMap + "Pr");
                        byte[] privateKeyBytes = Base64.decode(valuePr, Base64.DEFAULT);

                        KeyFactory kf = null;
                        try {
                            kf = KeyFactory.getInstance(alg);
                            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
                            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                            KeyPair keyPair = new KeyPair(publicKey, privateKey);

                            mapAsymmetricKeys.put(keyHashMap, keyPair);
                        } catch (Exception e) {
                            Log.d("ERR GENERATED ASY_KEY:", keyHashMap +" "+ e.getMessage() );
                            e.printStackTrace();
                        }
                    }
                }
        }
        else GenerateAsymnetricKeys();

    }

    /**************************************************************
     *
     *
     *
     *
     */

    private void GenerateAsymnetricKeys() {

        Store.clearStoreAsymmetricKey();


            for (String alg : AlgthCfg.algthmASymSign) {

                int[] keysSize = AlgthCfg.AsymmetrickeySize(alg,getProvidertoGenerateKey(alg));

                for (int k : keysSize) {

                    KeyPair keyPair = null;

                    try {
                        KeyPairGenerator kpg = null;
                      //  kpg = KeyPairGenerator.getInstance(alg,getProvidertoGenerateKey(alg));
                        kpg = KeyPairGenerator.getInstance(alg);
                        kpg.initialize(k);
                        keyPair = kpg.genKeyPair();

                        String keyHashMap = alg + k;

                        Log.d("OK GENERATED ASY_KEY:", keyHashMap+" Provider "+kpg.getProvider().getName());
                       // publishProgress(keyHashMap, kpg.getProvider().getName());

                        mapAsymmetricKeys.put(keyHashMap, keyPair);

                        Store.addAsymmetricKey(keyHashMap,keyPair);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

    }





    /*****************************************************************
     * Este método es necesario debido a que las claves creadas con cualquier proveedor
     * no son leidas por FlexiCore pero si a la inversa. Damos prioridad a todos los algoritmos
     * que implemente FlexiCore para que genere las claves.
     *
     * @param alg
     * @return
     */

    public  String getProvidertoGenerateKey(String alg){

        String provider="NULL";

        /*List<String> algthmSymmetric  = AlgthCfg.algthmSymHash.get(KEY_PROVIDER_FLEXICORE);
        List<String> algthmAsymmetric = AlgthCfg.algthmAsymHash.get(KEY_PROVIDER_FLEXICORE);

        if (algthmSymmetric.contains(alg) || algthmAsymmetric.contains(alg) ) provider = KEY_PROVIDER_FLEXICORE;
        else */
            provider = KEY_PROVIDER_DEFAULT; //OJO SI SE AMPLIA DEBERíA¡¡

        return provider;
    }

    protected void onProgressUpdate(String... progress) {
        String message;

        message   = progress[0]+" "+progress[1];

        if (PanelInfo!=null){

                PanelInfo.append("\n"+message);

        }

    }
}
