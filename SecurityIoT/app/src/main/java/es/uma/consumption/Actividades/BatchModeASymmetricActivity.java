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
import es.uma.consumption.Thread.AsymmetricThread;
import es.uma.consumption.Util.AlgthCfg;
import es.uma.consumption.Util.Store;


public class BatchModeASymmetricActivity extends Activity {

    protected  TextView Progress;


    Spinner     SpinnerAlgAsy, SpinnerOperation,SpinnerSize;
    Button      ASymmetrycButtonBatchMode;


    //String algthmOperationMode[] = {"ENCRYPT","DECRYPT","E_D","KEY","KEY SECURE","SIGN","VERIFY","S_V"};
    String algthmOperationMode[] = {"ENCRYPT","DECRYPT"};
   // String algthmOperationMode[] = {"ENCRYPT","DECRYPT","E_D"};

    String algSelected;
    String algthmAsymHash[];
    String algthmAsymPadding[];

    ConsumptionApplication application;

    int modeOperation;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_batch_mode_asymmetric);


            Progress = (TextView) findViewById(R.id.textViewProgressAsym);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            SpinnerAlgAsy        = (Spinner) findViewById(R.id.spinnerAlgASym);

            ArrayAdapter SpinnerAlgAsyAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AlgthCfg.algthmASymCipher);
            SpinnerAlgAsyAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerAlgAsy.setAdapter(SpinnerAlgAsyAdaptador);

            SpinnerOperation         = (Spinner) findViewById(R.id.spinnerOperationAsym);
            ArrayAdapter SpinnerAlgOperationAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, algthmOperationMode);
            SpinnerAlgOperationAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerOperation.setAdapter(SpinnerAlgOperationAdaptador);



            SpinnerSize= (Spinner) findViewById(R.id.spinnerSize);
            ArrayAdapter SpinnerSizeAdaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AlgthCfg.algthmSize );
            SpinnerSizeAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerSize.setAdapter(SpinnerSizeAdaptador);

             ASymmetrycButtonBatchMode = (Button)     findViewById(R.id.buttonAsymmetricMode);


            SpinnerOperation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int Operation=i;

                ArrayAdapter<String> SpinnerAlgSyAdaptador;

                if (Operation>2)
                   SpinnerAlgSyAdaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.spineer_item, AlgthCfg.algthmASymSign);
                else
                   SpinnerAlgSyAdaptador = new ArrayAdapter<String>(getApplicationContext(), R.layout.spineer_item, AlgthCfg.algthmASymCipher);

                SpinnerAlgSyAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinnerAlgAsy.setAdapter(SpinnerAlgSyAdaptador);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        application = (ConsumptionApplication) this.getApplication();

    }

    /*************************************************************************************************
     *
     * @param v
     */
    public void OnClickAsymm(View v){


        algSelected          = (String) SpinnerAlgAsy.getSelectedItem();

        algthmAsymPadding    = AlgthCfg.getAlgAsymmetricPadding(algSelected);
        algthmAsymHash       = AlgthCfg.getAlgAsymmetricHash(algSelected);

        modeOperation         = SpinnerOperation.getSelectedItemPosition();
        String modeOperationS = (String) SpinnerOperation.getSelectedItem();

        String sizeS          = (String) SpinnerSize.getSelectedItem();
        int    size           = Integer.parseInt(sizeS);

        boolean doIt = true;


        if ((modeOperationS.contentEquals(algthmOperationMode[1]))){ //DECRYPT
            if (Store.isEncryptedDataStored("SC", algSelected,algthmAsymPadding,size)) {
                Log.d("CIPHER", "CIPHER ASY OK");
                doIt = true;
            } else {
                doIt = false;
                Toast.makeText(this,"First Encrpyt", Toast.LENGTH_LONG).show();
            }
        }


       /* else{
            if ((modeOperationS.contentEquals(algthmOperationMode[6]))){    //VERIFY

                if (Store.isSignedDataStored(provider, algSelected,algthmAsymHash)) {
                    Log.d("CIPHER", "CIPHER ASY OK");
                    doIt = true;
                } else {
                    doIt = false;
                    Toast.makeText(this,"First Sign", Toast.LENGTH_LONG).show();
                }
            }

        }*/


        if (doIt) {

            ASymmetrycButtonBatchMode.setEnabled(false);
            Progress.setText("Progress...");


            AsymmetricThread symmetricBatch = new AsymmetricThread(algSelected,modeOperation,size,application);
            symmetricBatch.setVisual(ASymmetrycButtonBatchMode,Progress);
            symmetricBatch.execute();
        }

    }







}
