package com.souravbera.tensorflowliteinference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "model_7.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int[] INPUT_SIZE = {1, 20, 20, 3};

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Button button_result;
    private TextView text_output;
    private float[][] result;
    private float max=0.0f;
    private int pos=9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_output = (TextView) findViewById(R.id.model_out);
        button_result = (Button) findViewById(R.id.inference);
        initTensorFlowAndLoadModel();
        button_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = TensorflowClassifier.recognizeImage();
                for(int i=0;i<8;i++){
                    if(result[0][i]>max){
                        max= result[0][i];
                        pos=i;
                    }
                }

                text_output.setText(Arrays.deepToString(result));

            }
        });

    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    result = TensorflowClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

}
