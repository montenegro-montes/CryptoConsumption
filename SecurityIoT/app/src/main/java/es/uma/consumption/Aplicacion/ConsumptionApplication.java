package es.uma.consumption.Aplicacion;

import android.app.Application;
import android.util.Log;

import java.util.Arrays;

import es.uma.consumption.R;
import es.uma.consumption.Thread.KeysThread;
import es.uma.consumption.Util.Profiler;
import es.uma.consumption.Util.Store;
import es.uma.consumption.Util.Utils;


/**
 * Created by joseamontenegromontes on 10/12/16.
 */

public class ConsumptionApplication extends Application {

    public Profiler profiler;
    public KeysThread generateKeys;

    public byte [] quijote;

    public String infoPhone = null;


    public void onCreate() {
        super.onCreate();

        profiler = new Profiler(this);
        profiler.runProfillingService();

        Store.setSharedPreferences(this);

        infoPhone = Utils.phoneInformation(this);

        readData();
    }


    private void readData() {

        quijote = Utils.readRawTextFileByte(this, R.raw.quijote);

        //int numBloques = quijote.length/16;
        //int length     = numBloques*16;
        //int length     = 6*16; // 96 bytes
    }


    public byte [] getData(){
        int length     =  6*16; // 96 bytes
        return getData(length);
    }



  /*  public byte [] getData(int length){

        byte [] quijoteM16 = null;
        //Log.d("Info ","Length quixote: "+quijote.length);

        quijoteM16 = Arrays.copyOf(quijote, length);

        for (int i=0;i<length;i++) quijoteM16[i]=quijote[i];

        return quijoteM16;
    }*/

    public byte [] getData(int length){

        byte [] data = new byte[length];


        for (int i=0;i<length;i++) data[i]='a';

       // Log.d("Info ","data: "+length);

        return data;
    }



    public String getDataString(int length){

        byte [] data = new byte[length];


        for (int i=0;i<length;i++) data[i]='a';

        //Log.d("Info ","data: "+length);

        return new String(data);
    }
}
