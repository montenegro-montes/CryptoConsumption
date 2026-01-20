package es.uma.consumption.Actividades;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Security;

import es.uma.consumption.Aplicacion.ConsumptionApplication;
import es.uma.consumption.R;
import es.uma.consumption.Thread.KeysThread;
import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Store;
import iaik.security.provider.IAIK;

public class MainActivity extends Activity {


    ConsumptionApplication application;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    Button Sym,ASym,Clear,Hash;
    TextView PanelInfo;
    ScrollView mScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //verifyStoragePermissions(this);
        activateCryptoProviders();
        ProvidersList();

        Sym   = (Button) findViewById(R.id.buttonSymBatchMode);
        ASym  = (Button) findViewById(R.id.buttonAsymBatchMode);
        Clear = (Button) findViewById(R.id.buttonIDClearValues);
        Hash = (Button) findViewById(R.id.buttonHash);

        PanelInfo = (TextView) findViewById(R.id.textViewInfoPanel);
        mScrollView = (ScrollView) findViewById(R.id.SCROLLER_ID);

        PanelInfo.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                // you can add a toast or whatever you want here
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                //override stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                //override stub
            }
        });


        lanzarClaves();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }


    private void lanzarClaves(){
        application = (ConsumptionApplication) this.getApplication();
        application.generateKeys = new KeysThread(this,PanelInfo,Sym,ASym,Clear,Hash);
        application.generateKeys.execute();
    }


    public void onClick(View v){

        Intent intent=null;


       switch (v.getId()){

           case R.id.buttonIDClearValues:
                        Store.clearAllStores();
                        PanelInfo.setText("");
                        lanzarClaves();
                        Toast.makeText(this,"Store deleted",Toast.LENGTH_LONG).show();
                        break;

           case R.id.buttonSymBatchMode:
                        intent = new Intent(this, BatchModeSymmetricActivity.class);
                        break;

           case R.id.buttonAsymBatchMode:
                       intent = new Intent(this, BatchModeASymmetricActivity.class);
                       break;

           case R.id.buttonHash:
                        intent = new Intent(this, BatchModeCommActivity.class);
                        break;

       }

       if (intent!=null)  startActivity(intent);
    }

    private void activateCryptoProviders(){

        //IAIK.addAsProvider(false);
        //Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        //Security.addProvider(new de.flexiprovider.core.FlexiCoreProvider());

    }



/*
    public  void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }*/

    private void ProvidersList(){


        //for (Provider provider: Security.getProviders()) //ESTO POR SI CONSIDERO TODOS LOS PROVIDERS

        for(String provider: AlgthCfg.cryptoProvider) { // ME QUEDO CON ALGUNOS DE ELLOS
            AlgthCfg.checkCipher(provider, AlgthCfg.algthmSym,AlgthCfg.algthmASymSign);
            AlgthCfg.checkHash(provider,AlgthCfg.algorithmsHash);
        }




       /* Provider provider = Security.getProvider("FlexiCore");
        Set<Provider.Service> services = provider .getServices();
        for (Provider.Service service: services) {
            String type = service.getType();
            Log.i("CRYPTO","\ttype: " +type+" x---x "+service.getAlgorithm());
        }*/


       /* for (Provider provider: Security.getProviders()) {

            Provider provider = Security.getProvider("FlexiCore");
           Log.i("CRYPTO","provider: "+provider.getName());
            Set<Provider.Service> services = provider .getServices();
            for (Provider.Service service: services) {
                String type = service.getType();
                Log.i("CRYPTO","\t "+type+": " +service.getAlgorithm());
            }
        //}*/
    }



}
