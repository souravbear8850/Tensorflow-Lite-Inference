package com.souravbera.tensorflowliteinference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

//    private static final String MODEL_PATH = "model_7.tflite";
//    private static final String LABEL_PATH = "labels.txt";
//    private static final int[] INPUT_SIZE = {1, 20, 20, 3}
//    private Classifier classifier;
//    private Executor executor = Executors.newSingleThreadExecutor();
    private Button button_result;
    private TextView text_output;
    private float[][] result;
    private MainActivity mainActivity= new MainActivity();
    private SensorRawDataAG sensorRawDataAG= new SensorRawDataAG(mainActivity);
    TextView xaValue,yaValue,zaValue,xgValue,ygValue,zgValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_output = (TextView) findViewById(R.id.model_out);
        button_result = (Button) findViewById(R.id.inference);
        xaValue= (TextView) findViewById(R.id.xaVal);
        yaValue= (TextView) findViewById(R.id.yaVal);
        zaValue= (TextView) findViewById(R.id.zaVal);
        xgValue= (TextView) findViewById(R.id.xgVal);
        ygValue= (TextView) findViewById(R.id.ygVal);
        zgValue= (TextView) findViewById(R.id.zgVal);

//        initTensorFlowAndLoadModel();
        button_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                result = tensorFlowClassifier.recognizeImage();
                    result= sensorRawDataAG.onResume1();
                text_output.setText(Arrays.deepToString(result));

            }
        });


    }

//    private void initTensorFlowAndLoadModel() {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    classifier = tensorFlowClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE);
//                } catch (final Exception e) {
//                    throw new RuntimeException("Error initializing TensorFlow!", e);
//                }
//            }
//        });
//    }

}
