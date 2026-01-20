package es.uma.consumption.Util;

import android.util.Log;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by monte on 9/2/17.
 */

public class AlgthCfg {

    public static int PROVIDER_IAIK         = 0;
    public static int PROVIDER_BC           = 1;
    public static int PROVIDER_SC           = 2;
   // public static int PROVIDER_FLEXICORE    = 3;

    public static String cryptoProvider[]                   = {"SC"};// {"IAIK","BC","SC"};////"FlexiCore"};
    public static Map<String, List<String>> algthmSymHash   = new HashMap<String, List<String> >();
    public static Map<String, List<String>> algthmAsymHash  = new HashMap<String, List<String> >();
    public static Map<String, List<String>> algthmHashHash  = new HashMap<String, List<String> >();

    public static String algthmSym[]            = {"AES"};//{"AES","RC4","BLOWFISH","DES","DESede"};
    public static String algthmASymCipher[]     = {"RSA"};
    public static String algthmASymSign[]       = {"RSA"};//{"DSA","RSA"};

    public static String algthmSize[]       = {"10","100","200"};
    public static String CommSize[]       = {"10","100","1000","2000"};


    private static int [][] KeySizeSym        = new int[algthmSym.length][];
    private static int [][] KeySizeAsym       = new int[algthmASymSign.length][];


   // private static String Hash_RSA[]            = {"NONE","MD5","SHA1","SHA256","SHA384","SHA512"};
    private static String Hash_RSA[]            = {"NONE","SHA1"};


    public static String[] algorithmsHash           = {"MD5","SHA1","SHA224","SHA256","SHA384","SHA512"};
    public static String[] algorithmsMAC            = {"HmacMD5","HmacSHA1","HmacSHA224","HmacSHA256","HmacSHA384","HmacSHA512"};





    public static int [] SymmetrickeySize(String alg,String provider){

        KeySizeSym =new int[algthmSym.length][];

        if (provider.contentEquals("FlexiCore")){
            KeySizeSym[0] = new int[]{128, 192, 256};//AES
            KeySizeSym[1] = new int[]{};//RC4
            KeySizeSym[2] = new int[]{};//Blowfish
            KeySizeSym[3] = new int[]{};//DES
            KeySizeSym[4] = new int[]{112, 168};//DESede
        }
        else{
            KeySizeSym[0] = new int[]{128, 192, 256};//AES
          /*  KeySizeSym[1] = new int[]{128, 256, 512, 1024};//RC4
            KeySizeSym[2] = new int[]{128, 192, 256};//Blowfish
            KeySizeSym[3] = new int[]{64};//DES
            KeySizeSym[4] = new int[]{112, 168};//DESede*/
        }

        int selected=-1;

        if (alg.contentEquals("AES"))           selected=0;
       /* else if (alg.contentEquals("RC4"))      selected=1;
        else if (alg.contentEquals("BLOWFISH")) selected=2;
        else if (alg.contentEquals("DES"))      selected=3;
        else if (alg.contentEquals("DESede"))   selected=4;*/


        return KeySizeSym[selected];
    }

    /********************************************************************+
     *
     * @param alg
     * @param provider
     * @return
     */
    public static int [] AsymmetrickeySize(String alg,String provider){


            if (provider.contentEquals("BC")){
               KeySizeAsym[0] = new int[]{1024};//DSA
              // KeySizeAsym[1] = new int[]{512,1024, 2048};//RSA
                KeySizeAsym[1] = new int[]{512};//RSA
            }
            else {
             //   KeySizeAsym[0] = new int[]{1024,2048};//DSA
              //  KeySizeAsym[1] = new int[]{512,1024, 2048};//RSA
                KeySizeAsym[0] = new int[]{512};//RSA
            }


        int selected=-1;

        //if (alg.contentEquals("DSA"))           selected=0;
        //else
        if (alg.contentEquals("RSA"))      selected=0;

        return KeySizeAsym[selected];
    }

    /********************************************************************+
     *
     * @param alg
     * @return
     */

    public static String[] getAlgSymmetricMode(String alg){
        if(alg.contentEquals("RC4"))  return new String[]{"ECB"};
      //  else                          return new String[]{"CBC","CFB","CTR","CTS","ECB","OFB"};
        else                          return new String[]{"CBC","ECB"};

    }

    public static String[] getAlgAsymmetricMode(String alg){

        if(alg.contentEquals("RSA"))  return new String[]{"NONE"};//return new String[]{"NONE","ECB"};

        return null;
    }

    /********************************************************************+
     *
     * @param alg
     * @return
     */

    public static String[] getAlgSymmetricPadding(String alg){

         if(alg.contentEquals("RC4")) return  new String[]{"NoPadding"};
         //else                         return  new String[] {"NoPadding","ISO10126Padding","PKCS5Padding"};
                                 return  new String[] {"PKCS5Padding","ZeroBytePadding"};
    }

    /********************************************************************+
     *
     * @param alg
     * @return
     */

     public static String[] getAlgAsymmetricPadding(String alg){

        String []padding= {};

         //if (alg.contentEquals("RSA")) padding = new String[]{"NoPadding", "OAEPPadding", "OAEPwithSHA-1andMGF1Padding", "OAEPwithSHA-256andMGF1Padding", "PKCS1Padding"};
         //if (alg.contentEquals("RSA")) padding = new String[]{"NoPadding", "PKCS1Padding"};
         if (alg.contentEquals("RSA")) padding = new String[]{"NoPadding"};
        return padding;
    }


    public static String[] getAlgAsymmetricHash(String alg){

        String []hash= {};

        if (alg.contentEquals("RSA")) hash = Hash_RSA;


        return hash;
    }

    public static void checkCipher (String providerS,String []algthmSm,String []algthmASym){

        List<String> algOKSy = check ("Cipher", providerS,algthmSm);
        algthmSymHash.put(providerS,algOKSy);

        List<String> algOKAsy = check ("KeyPairGenerator", providerS,algthmASym);
        algthmAsymHash.put(providerS,algOKAsy);
    }

    public static void checkHash (String providerS,String []algthmHash) {

        List<String> algOKHash = AlgthCfg.check("MessageDigest",providerS,algthmHash);
        algthmHashHash.put(providerS,algOKHash);
    }

    public static  List<String> check (String type,String providerS,String []algthms){

        List<String> algOK = new ArrayList<String>();

        for (String alg:algthms) {

            Provider provider = Security.getProvider(providerS);

            if (provider!=null){
                Provider.Service service =provider.getService(type, alg);

                if (service==null){
                    Log.d("INFO ER",alg +" not "+providerS);
                }
                else{
                    algOK.add(alg);
                    Log.d("INFO OK",alg +" in  "+providerS);
                }
            }
        }

        return algOK;
    }

    public static  void checkAll (){

        for (Provider p : Security.getProviders()) {
            for (Provider.Service s : p.getServices()) {
                Log.d("XX","Security provider "+s.getClassName()+" for "+s.getAlgorithm());
            }
        }
    }

}


