package es.uma.consumption.Actividades;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

import javax.crypto.SecretKey;

import es.uma.consumption.Aplicacion.ConsumptionApplication;
import es.uma.consumption.R;
import es.uma.consumption.Thread.CommThread;
import es.uma.consumption.Util.AlgthCfg;


public class BatchModeCommActivity extends Activity {

    TextView Progress;
    Spinner SpinnerAlgMode,SpinnerSizeComm;
    Button ButtonHASH;

    String [] CoMM_MODE =  new String[]{"WIFI","BLUE"};
    ConsumptionApplication application;
    Map<String, SecretKey> mapMACKeys;
    int modeOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_mode_comm);


        SpinnerAlgMode        = (Spinner) findViewById(R.id.spinnerAlgMode);

        ArrayAdapter SpinnerAlgMacAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CoMM_MODE);
        SpinnerAlgMacAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerAlgMode.setAdapter(SpinnerAlgMacAdaptador);

        SpinnerSizeComm = (Spinner) findViewById(R.id.spinnerSizeComm);

        ArrayAdapter SpinnerSizeAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AlgthCfg.CommSize );
        SpinnerSizeAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerSizeComm.setAdapter(SpinnerSizeAdaptador);


        Progress = (TextView) findViewById(R.id.textViewProgressHashMap);
        ButtonHASH = (Button) findViewById(R.id.buttonMacHash);




        application = (ConsumptionApplication) this.getApplication();

    }


    public void onClick(View v) {

        modeOperation         = SpinnerAlgMode.getSelectedItemPosition();
        int posSize= SpinnerSizeComm.getSelectedItemPosition();


        int size =  10;
        switch (posSize){
            case 0: size=10; break;
            case 1: size=100; break;
            case 2: size=1000; break;
            case 3: size=2000; break;
        }


        ButtonHASH.setEnabled(false);
        Progress.setText("Progress...");

         CommThread symmetricBatch = new CommThread("192.168.0.157",6666,modeOperation,size,application);
         symmetricBatch.setVisual(ButtonHASH,Progress);
         symmetricBatch.execute();


    }



 }
