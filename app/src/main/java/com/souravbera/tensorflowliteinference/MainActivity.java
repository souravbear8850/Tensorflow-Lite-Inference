package com.souravbera.tensorflowliteinference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.souravbera.tensorflowliteinference.tensorflowLite.Classifier;
import com.souravbera.tensorflowliteinference.tensorflowLite.TensorflowClassifier;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "model.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int[] INPUT_SIZE = {1, 20, 20, 3};
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Button btn_result;
    private TextView text_output;
    private float[][] result= new float[1][8];
    private TensorflowClassifier tensorFlowClassifier= new TensorflowClassifier();
    private TextView xaValue,yaValue,zaValue,xgValue,ygValue,zgValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_output = (TextView) findViewById(R.id.text_out) ;
        btn_result = (Button) ;
        xaValue= (TextView) findViewById(R.id.xaVal);
        yaValue= (TextView) findViewById(R.id.yaVal);
        zaValue= (TextView) findViewById(R.id.zaVal);
        xgValue= (TextView) findViewById(R.id.xgVal);
        ygValue= (TextView) findViewById(R.id.ygVal);
        zgValue= (TextView) findViewById(R.id.zgVal);

        initTensorFlowAndLoadModel();
        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = tensorFlowClassifier.recognizeImage();
                text_output.setText(Arrays.deepToString(result));

            }
        });


    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = tensorFlowClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

}
