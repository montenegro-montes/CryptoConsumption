package es.uma.consumption.Actividades;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import es.uma.consumption.Aplicacion.ConsumptionApplication;
import es.uma.consumption.R;
import es.uma.consumption.Thread.SymmetricThread;
import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Store;


public class BatchModeSymmetricActivity extends Activity {

    protected  TextView Progress;


    Spinner     SpinnerAlgSy, SpinnerOperation,SpinnerSize;
    Button      SimmetrycButtonBatchMode;



    String algSelected;
    //String algthmOperationMode[] = {"ENCRYPT","DECRYPT","BOTH"};
    String algthmOperationMode[] = {"ENCRYPT","DECRYPT"};

    String algthmSymMode[];
    String algthmSymPadding[];



    ConsumptionApplication application;
    int modeOperation;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_batch_mode_symmetric);



            Progress = (TextView) findViewById(R.id.textViewProgressSym);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            SpinnerAlgSy        = (Spinner) findViewById(R.id.spinnerAlgSym);


            ArrayAdapter SpinnerAlgSyAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AlgthCfg.algthmSym);
            SpinnerAlgSyAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerAlgSy.setAdapter(SpinnerAlgSyAdaptador);

            SpinnerOperation = (Spinner) findViewById(R.id.spinnerOperationSym);
            ArrayAdapter SpinnerAlgModedaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, algthmOperationMode);
            SpinnerAlgModedaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerOperation.setAdapter(SpinnerAlgModedaptador);



            SpinnerSize= (Spinner) findViewById(R.id.spinnerSize);
            ArrayAdapter SpinnerSizeAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AlgthCfg.algthmSize );
            SpinnerSizeAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerSize.setAdapter(SpinnerSizeAdaptador);



            SimmetrycButtonBatchMode    = (Button)     findViewById(R.id.buttonSymmetricMode);





        application = (ConsumptionApplication) this.getApplication();

    }

    /*************************************************************************************************
     *
     *
     *
     *
     * @param v
     */


    public void OnClick(View v) {


        algSelected = (String) SpinnerAlgSy.getSelectedItem();

        algthmSymMode       = AlgthCfg.getAlgSymmetricMode(algSelected);
        algthmSymPadding    = AlgthCfg.getAlgSymmetricPadding(algSelected);

        modeOperation         = SpinnerOperation.getSelectedItemPosition();
        String modeOperationS = (String) SpinnerOperation.getSelectedItem();

        String sizeS          = (String) SpinnerSize.getSelectedItem();
        int    size           = Integer.parseInt(sizeS);

        boolean doIt = true;


        if ((modeOperationS.contentEquals("DECRYPT"))){
            if (Store.isEncryptedDataStored("SC", algSelected,algthmSymMode,algthmSymPadding,size)) {
                Log.d("CIPHER", "CIPHER OK");
                doIt = true;
            } else {
                doIt = false;
                Toast.makeText(this,"First Encrpyt", Toast.LENGTH_LONG).show();
            }
        }


        if (doIt) {

            SimmetrycButtonBatchMode.setEnabled(false);
            Progress.setText("Progress...");


            SymmetricThread symmetricBatch = new SymmetricThread(algSelected,modeOperation,size,application);
            symmetricBatch.setVisual(SimmetrycButtonBatchMode,Progress);
            symmetricBatch.execute();
        }
    }



}
