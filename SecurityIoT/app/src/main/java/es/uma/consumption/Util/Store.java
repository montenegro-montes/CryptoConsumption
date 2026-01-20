package es.uma.consumption.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.KeyPair;
import java.util.Map;

import javax.crypto.SecretKey;

import es.uma.consumption.Aplicacion.ConsumptionApplication;


public class Store {

    private  static String SYMMETRIC_KEYS_DATA  = "KEYS";
    private  static String ASYMMETRIC_KEYS_DATA = "KEYS_ASY";
    private  static String MAC_KEYS_DATA        = "KEYS_MAC";
    private  static String ENCRYPT_DATA         = "ENCRYPT";
    private  static String SIGNED_DATA          = "SIGN";




    static ConsumptionApplication _application;

    /********************************
     *
     * @param application
     */

     static public void setSharedPreferences(ConsumptionApplication application){

        _application = application;

    }

    /********************************
     *
     * @return
     */

     static public Map getSymmetricKeys (){

        return getKeys(SYMMETRIC_KEYS_DATA);
    }

    /********************************
     *
     * @return
     */

     static public Map getASymmetricKeys (){

        return getKeys(ASYMMETRIC_KEYS_DATA);
    }

    static public Map getMACKeys (){

        return getKeys(MAC_KEYS_DATA);
    }

    /********************************
     *
     * @param value
     * @return
     */

     static private Map getKeys (String value){
        android.content.SharedPreferences sharedPref = _application.getSharedPreferences(value, Context.MODE_PRIVATE);
        Map<String,?> keys = sharedPref.getAll();

        return keys;
     }


    /********************************
     *
     */

     static public void clearAllStores() {
        clearStoreSymmetricKey();
        clearStoreAsymmetricKey();
        clearStoreEncrypted();
         clearStoreSigned();
         clearStoreMACKey();
    }

    /********************************
     *
     */

     static public void  clearStoreSymmetricKey(){

        clearStore(SYMMETRIC_KEYS_DATA);
    }

    /********************************
     *
     */

     static public void  clearStoreAsymmetricKey(){

         clearStore(ASYMMETRIC_KEYS_DATA);
    }

    static public void  clearStoreMACKey(){

        clearStore(MAC_KEYS_DATA);
    }

    /********************************
     *
     */

     static public void  clearStoreEncrypted(){

         clearStore(ENCRYPT_DATA);
     }

    static public void  clearStoreSigned(){

        clearStore(SIGNED_DATA);
    }


    /********************************
     *
     * @param store
     * @return
     */


     static private void  clearStore(String store){

        SharedPreferences sharedPref = _application.getSharedPreferences(store, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear(); editor.commit(); //Borro anteriores.

    }

    /********************************
     *
     * @param key
     * @param value
     */


     static public void addSymmetricKey(String key, SecretKey value){

         SharedPreferences sharedPref = _application.getSharedPreferences(SYMMETRIC_KEYS_DATA, Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPref.edit();

         String keyS = Base64.encodeToString(value.getEncoded(), Base64.DEFAULT);
         editor.putString(key, keyS);
         editor.commit();

    }

    static public void addMacKey(String key, SecretKey value){

        SharedPreferences sharedPref = _application.getSharedPreferences(MAC_KEYS_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String keyS = Base64.encodeToString(value.getEncoded(), Base64.DEFAULT);
        editor.putString(key, keyS);
        editor.commit();

    }

    /********************************
     *
     * @param key
     * @param value
     */

    static public void addAsymmetricKey(String key, KeyPair value){

        SharedPreferences sharedPref = _application.getSharedPreferences(ASYMMETRIC_KEYS_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        byte[] keyPr = value.getPrivate().getEncoded();
        byte[] keyPu = value.getPublic().getEncoded();

        String keySPr = Base64.encodeToString(keyPr, Base64.DEFAULT);
        String keySPu = Base64.encodeToString(keyPu, Base64.DEFAULT);

        editor.putString(key+ "Pr", keySPr);
        editor.putString(key+ "Pu", keySPu);

        editor.commit();

    }

    /********************************
     *
     * @param provider
     * @param algS
     * @param algthmSymMode
     * @param algthmSymPadding
     * @return
     */

    static public boolean isEncryptedDataStored(String provider,String algS,
                                                String []algthmSymMode,String []algthmSymPadding,int size){

        String AlgoritmMode = algS + "/" + algthmSymMode[0] + "/" + algthmSymPadding[0];
        int[] keysSize      = AlgthCfg.SymmetrickeySize(algS, provider);

        String keyHashMap =  provider+AlgoritmMode+keysSize[0]+size;
        SharedPreferences sharedPref = _application.getSharedPreferences(ENCRYPT_DATA, Context.MODE_PRIVATE);
        return sharedPref.contains(keyHashMap);
    }

    static public boolean isEncryptedDataStored(String provider,String algS,String []algthmAsymPadding,int size){

        String AlgoritmMode = algS + "/NONE/" + algthmAsymPadding[0];
        int[] keysSize      = AlgthCfg.AsymmetrickeySize(algS, provider);

        String keyHashMap =  provider+AlgoritmMode+keysSize[0]+size;
        SharedPreferences sharedPref = _application.getSharedPreferences(ENCRYPT_DATA, Context.MODE_PRIVATE);
        return sharedPref.contains(keyHashMap);
    }

    static public boolean isSignedDataStored(String provider,String algS,String []algthmHash){
        String AlgoritmMode = null;
        String keyHashMap   = null;

        SharedPreferences sharedPref = _application.getSharedPreferences(SIGNED_DATA, Context.MODE_PRIVATE);
        int[] keysSize      = AlgthCfg.AsymmetrickeySize(algS, provider);

        for (String hash: algthmHash){
            AlgoritmMode = hash + "with" + algS;
            keyHashMap =  provider+AlgoritmMode+keysSize[0];

            if (sharedPref.contains(keyHashMap)) return true;
        }

        /*Map<String,?> prox = sharedPref.getAll();

        System.out.println("BUSCANDO: "+keyHashMap);
        for (Map.Entry<String, ?> entry : prox.entrySet()) {
            System.out.println("clave=" + entry.getKey());// + ", valor=" + entry.getValue());
        }*/

        return false;
    }

    /********************************
     *
     * @param provider
     * @param AlgoritmMode
     * @param keySize
     * @param encrypted_text
     */


    static public void setEncrpytedText(String provider,String AlgoritmMode, int keySize,byte []encrypted_text,int size){
        setText( provider, AlgoritmMode,  keySize,encrypted_text, ENCRYPT_DATA,size);
    }

    static public  byte [] getEncrpytedText(String provider,String AlgoritmMode, int keySize,int size){

        return getText( provider, AlgoritmMode, keySize, ENCRYPT_DATA,size);
    }


    static private void setText(String provider,String AlgoritmMode, int keySize,byte []text,String SHARED_PREFERENCE,int size){
        SharedPreferences sharedPref = _application.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        String keyHashMap =  provider+AlgoritmMode+keySize+size;
        if(!sharedPref.contains(keyHashMap)) {
            String keyS = Base64.encodeToString(text, Base64.DEFAULT);
            editor.putString(keyHashMap, keyS);
            editor.commit();
        }
        else editor.commit();

    }


    static private  byte [] getText(String provider,String AlgoritmMode, int keySize,String SHARED_PREFERENCE,int size){
        SharedPreferences sharedPref = _application.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);

        byte [] signed_text=null;
        String keyHashMap =  provider+AlgoritmMode+keySize+size ;

        if (sharedPref.contains(keyHashMap)){
            String value= sharedPref.getString(keyHashMap,null);
            if (value!=null)
                signed_text = Base64.decode(value, Base64.DEFAULT);
        }

        return signed_text;
    }
}
