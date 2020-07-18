package com.souravbera.tensorflowliteinference;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.lang.Math;
import java.util.PriorityQueue;
import java.util.Random;


public class TensorflowClassifier implements Classifier {

    private static final int MAX_RESULTS = 3;
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;
    private static final float THRESHOLD = 0.1f;
    private static TensorflowClassifier classifier = new TensorflowClassifier();

    private Interpreter interpreter;
    private int[] inputSize;
    private List<String> labelList;
    private float[][] result;

    public TensorflowClassifier() {

    }

    static float[][] create(AssetManager assetManager, String modelPath, String labelPath, int[] inputSize) throws IOException {
        classifier.interpreter = new Interpreter(classifier.loadModelFile(assetManager, modelPath), new Interpreter.Options());
        classifier.labelList = classifier.loadLabelList(assetManager, labelPath);
        classifier.inputSize = inputSize;

        classifier.result = recognizeImage();
        return classifier.result;
    }


    public static float[][] recognizeImage() {

        List<String> labelList = classifier.labelList;
        Interpreter interpreter = classifier.interpreter;
        Random r = new Random();
        int low = 10;
        int high = 100;

        float[][][][] input = new float[1][20][20][3];


        for (int j = 0; j < 20; j++) {
            for (int k = 0; k < 20; k++) {
                for (int l = 0; l < 3; l++) {
                    input[0][j][k][l]= (float) r.nextInt(high-low)+low;
//                    input[0][j][k][l] = 200.0f;
                    input[0][j][k][l] = (float) (input[0][j][k][l] / 255.0) * 2 - 1;
                }
            }
        }

//            labelList=loadLabelList(assetManager, labelPath);
        float[][] result = new float[1][labelList.size()];
        interpreter.run(input, result);

        return result;

    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }


    @Override
    public void close() {
        interpreter.close();
        interpreter = null;
    }
}
