package com.souravbera.tensorflowliteinference;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.souravbera.tensorflowliteinference.tensorflowLite.Classifier;
import com.souravbera.tensorflowliteinference.tensorflowLite.TensorflowClassifier;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SensorRawDataAG  {

    private static final String TAG = "SensorRawDataAG";
    private static final String MODEL_PATH = "model_7.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int[] INPUT_SIZE = {1, 20, 20, 3};

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private final SensorManager mSensorManager;
    Sensor mAccelerometer, mGyroscope;
    private float acc, acc1;
    private float gyro, gyro1;

    private int countAcc=0;
    private int countGyro=0;

    private float[][] result;

    private float[][][][] data= new float[1][20][20][3];
    private Activity mActivity;


    private TensorflowClassifier tensorFlowClassifier= new TensorflowClassifier();

    public SensorRawDataAG(Activity mActivity) {
        this.mActivity=mActivity;
        Log.d(TAG, "onCreate: Intialising Sensor Services");
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        initTensorFlowAndLoadModel();

    }

    protected float[][] onResume1() {

//       super.onResume();
        mSensorManager.registerListener(new SensorEventListener() {
            private  long time= System.currentTimeMillis();
            private int countx=0,county=0;
            @Override
            public void onSensorChanged(final SensorEvent sAccEvent) {


                mSensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sGyroEvent) {

                        //----------------------------------------------
                        gyro = (float) Math.sqrt(sGyroEvent.values[0] * sGyroEvent.values[0] + sGyroEvent.values[1] * sGyroEvent.values[1] + sGyroEvent.values[2] * sGyroEvent.values[2]);

                        acc = (float) Math.sqrt(sAccEvent.values[0] * sAccEvent.values[0] + sAccEvent.values[1] * sAccEvent.values[1] + sAccEvent.values[2] * sAccEvent.values[2]);
                        if(countx<10){
                            if(county<10) {
//                                storeData(countx, county, sAccEvent.values[0], sAccEvent.values[1], sAccEvent.values[2], sGyroEvent.values[0], sGyroEvent.values[1], sGyroEvent.values[2]);
                                data[0][countx][county][0]=sAccEvent.values[0];
                                data[0][countx][county][1]=sAccEvent.values[1];
                                data[0][countx][county][2]=sAccEvent.values[0];
                                data[0][countx+10][county+10][0]=sGyroEvent.values[0];
                                data[0][countx+10][county+10][1]=sGyroEvent.values[0];
                                data[0][countx+10][county+10][2]=sGyroEvent.values[0];
                                county += 1;
                            }
                            else{
                                county=0;
                                countx+=1;
                            }
                        }
                        else{
                            Log.d(TAG,"data: "+data[0][0][0][0]);
                            countx=0;
                        }
                        if (acc < 2.8) {
                            long start = System.currentTimeMillis();
                            do {
                                acc1 = (float) Math.sqrt(sAccEvent.values[0] * sAccEvent.values[0] + sAccEvent.values[1] * sAccEvent.values[1] + sAccEvent.values[2] * sAccEvent.values[2]);
                                gyro1= (float) Math.sqrt(sGyroEvent.values[0]*sGyroEvent.values[0]+sGyroEvent.values[1] * sGyroEvent.values[1] + sGyroEvent.values[2] * sGyroEvent.values[2]);


                                if(acc1 > 20 && gyro1 > 6){
                                    result = tensorFlowClassifier.recognizeImage(data);
                                    break;
                                }

                            } while ((System.currentTimeMillis() - start) < 500);
                        }
                        //----------------------------------------------

                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                }, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
//                acc = (float) Math.sqrt(sAccEvent.values[0] * sAccEvent.values[0] + sAccEvent.values[1] * sAccEvent.values[1] + sAccEvent.values[2] * sAccEvent.values[2]);
//                if (acc < 2.8) {
//                    long start = System.currentTimeMillis();
//                    do {
//                        acc1 = (float) Math.sqrt(sAccEvent.values[0] * sAccEvent.values[0] + sAccEvent.values[1] * sAccEvent.values[1] + sAccEvent.values[2] * sAccEvent.values[2]);
//                        if (acc1 > 20) {
//
//                            break;
//                        }
//                    } while ((System.currentTimeMillis() - start) < 500);
//                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

//        mSensorManager.registerListener(new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent sGyroEvent) {
//                gyro = (float) Math.sqrt(sGyroEvent.values[0] * sGyroEvent.values[0] + sGyroEvent.values[1] * sGyroEvent.values[1] + sGyroEvent.values[2] * sGyroEvent.values[2]);
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//        }, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);return
        return result;
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = tensorFlowClassifier.create(mActivity.getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

//    private void storeData(int countx,int county,float accx,float accy,float accz,float gyrox,float gyroy,float gyroz){
//        data[0][countx][county][0]=accx;
//        data[0][countx][county][1]=accy;
//        data[0][countx][county][2]=accz;
//        data[0][countx+10][county+10][0]=gyrox;
//        data[0][countx+10][county+10][1]=gyroy;
//        data[0][countx+10][county+10][2]=gyroz;
//    }
//    private float[][] storeSensorData(float time){
//        final float[][] data= new float[1][1200];
//        countGyro=0;
//        countAcc=0;
//        long start= System.currentTimeMillis();
//
//            mSensorManager.registerListener(new SensorEventListener() {
//                @Override
//                public void onSensorChanged(final SensorEvent SAccEvent) {
//
//                    mSensorManager.registerListener(new SensorEventListener() {
//                        @Override
//                        public void onSensorChanged(SensorEvent SGyroEvent) {
////                    if(System.currentTimeMillis()- start)<2000){
////
////                            }
//                                    data[0][countAcc]=SAccEvent.values[0];
//                                    data[0][countAcc+1]=SAccEvent.values[1];
//                                    data[0][countAcc+2]=SAccEvent.values[2];
//
//                            data[0][countGyro]=SGyroEvent.values[0];
//                            data[0][countGyro+1]=SGyroEvent.values[1];
//                            data[0][countGyro+2]=SGyroEvent.values[2];
//
//                            countGyro=countGyro+3;
//                            countAcc=countAcc+3;
//
//
//                        }
//
//                        @Override
//                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//                        }
//                    },mGyroscope,SensorManager.SENSOR_DELAY_FASTEST);
//                }
//
//                @Override
//                public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//                }
//            },mAccelerometer,SensorManager.SENSOR_DELAY_FASTEST);
//
//
//        return data;
//
//    }



    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(this);

    }


}