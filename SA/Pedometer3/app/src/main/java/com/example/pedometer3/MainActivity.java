package com.example.pedometer3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView showX;
    private TextView showY;
    private TextView showZ;
    private TextView showA;
    private TextView showStep;
    private Button startWrite;
    private Button stopWrite;
    private Button start;
    private Button pause;
    private Button reset;

    SensorManager sensorManager;
    private boolean doWrite;
    private boolean doStep;

    //存放三轴数据
    //float[] oriValues = new float[3];
    final int ValueNum = 4;
    //用于存放计算阈值的波峰波谷差值
    float[] tempValue = new float[ValueNum];
    int tempCount = 0;
    //是否上升的标志位
    boolean isDirectionUp = false;
    //持续上升次数
    int continueUpCount = 0;
    //上一点的持续上升的次数，为了记录波峰的上升次数
    int continueUpFormerCount = 0;
    //上一点的状态，上升还是下降
    boolean lastStatus = false;
    //波峰值
    float peakOfWave = 0;
    //波谷值
    float valleyOfWave = 0;
    //此次波峰的时间
    long timeOfThisPeak = 0;
    //上次波峰的时间
    long timeOfLastPeak = 0;
    //当前的时间
    long timeOfNow = 0;
    //当前传感器的值
    float gravityNew = 0;
    //上次传感器的值
    float gravityOld = 0;
    //动态阈值需要动态的数据，这个值用于这些动态数据的阈值
    final float InitialValue = (float) 1.3;
    //初始阈值
    float ThreadValue = (float) 2.0;
    //波峰波谷时间差
    int TimeInterval = 250;
    //    private int count = 0;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showX=findViewById(R.id.showX);
        showY=findViewById(R.id.showY);
        showZ=findViewById(R.id.showZ);
        showA=findViewById(R.id.showA);
        showStep=findViewById(R.id.showStep);
        startWrite=findViewById(R.id.startWrite);
        stopWrite=findViewById(R.id.stopWrite);
        start=findViewById(R.id.start);
        pause=findViewById(R.id.pause);
        reset=findViewById(R.id.reset);

        doWrite=false;
        doStep=false;
        addListeners();
        //获取SensorManager实例
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //获取Sensor传感器类型
        Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //注册Listener
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
    }
    private SensorEventListener sensorEventListener=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float X=event.values[0];
            float Y = event.values[1];
            float Z = event.values[2];
            double A = Math.sqrt(X * X + Y * Y + Z * Z);
            DecimalFormat df = new DecimalFormat("#,##0.000");
            String s_x=df.format(X);
            String s_y=df.format(Y);
            String s_z=df.format(Z);
            String s_a=df.format(A);
            showX.setText(s_x);
            showY.setText(s_y);
            showZ.setText(s_z);
            showA.setText(s_a);
            detectorNewStep((float) A);

            String message="x:"+s_x+" y:"+s_y+" z:"+s_z+" a:"+s_a+"\n";
            if (doWrite){
                writeFile(message);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private void addListeners(){
        startWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWrite=true;
                Log.e("write","start");
            }
        });
        stopWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWrite=false;
                Log.e("write","stop");
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStep=true;
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStep=false;
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStep=false;
                mCount=0;
                showStep.setText("0");
            }
        });
    }
    private void writeFile(String _message){
        try {
            String path=getExternalFilesDir("")+"/acc.txt";
            SimpleDateFormat format=new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]-->");
            Date curDate=new Date(System.currentTimeMillis());
            String strDate=format.format(curDate);
            String message=strDate+_message;
            File file = new File(path);
            if (!file.exists()) {
                Log.d("file","new");
                boolean status=file.createNewFile();
                Log.d("create new file ",status?"success":"failed");
            }
            RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(message);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------
    /*
     * 检测步子，并开始计步
     * 1.传入sensor中的数据
     * 2.如果检测到了波峰，并且符合时间差以及阈值的条件，则判定为1步
     * 3.符合时间差条件，波峰波谷差值大于initialValue，则将该差值纳入阈值的计算中
     * */
    public void detectorNewStep(float values) {
        if (gravityOld == 0) {
            gravityOld = values;
        } else {
            if (detectorPeak(values, gravityOld)) {
                timeOfLastPeak = timeOfThisPeak;
                timeOfNow = System.currentTimeMillis();
                if (timeOfNow - timeOfLastPeak >= TimeInterval
                        && (peakOfWave - valleyOfWave >= ThreadValue)) {
                    timeOfThisPeak = timeOfNow;
                    /*
                     * 更新界面的处理，不涉及到算法
                     * 一般在通知更新界面之前，增加下面处理，为了处理无效运动：
                     * 1.连续记录10才开始计步
                     * 2.例如记录的9步用户停住超过3秒，则前面的记录失效，下次从头开始
                     * 3.连续记录了9步用户还在运动，之前的数据才有效
                     * */
                    if (doStep){
                        mCount++;
                        showStep.setText(String.valueOf(mCount));
                    }
                }
                if (timeOfNow - timeOfLastPeak >= TimeInterval
                        && (peakOfWave - valleyOfWave >= InitialValue)) {
                    timeOfThisPeak = timeOfNow;
                    ThreadValue = peakValleyThread(peakOfWave - valleyOfWave);
                }
            }
        }
        gravityOld = values;
    }

    /*
     * 检测波峰
     * 以下四个条件判断为波峰：
     * 1.目前点为下降的趋势：isDirectionUp为false
     * 2.之前的点为上升的趋势：lastStatus为true
     * 3.到波峰为止，持续上升大于等于2次
     * 4.波峰值大于20
     * 记录波谷值
     * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
     * 2.所以要记录每次的波谷值，为了和下次的波峰做对比
     * */
    public boolean detectorPeak(float newValue, float oldValue) {
        lastStatus = isDirectionUp;
        if (newValue >= oldValue) {
            isDirectionUp = true;
            continueUpCount++;
        } else {
            continueUpFormerCount = continueUpCount;
            continueUpCount = 0;
            isDirectionUp = false;
        }

        if (!isDirectionUp && lastStatus
                && (continueUpFormerCount >= 2 || oldValue >= 20)) {
            peakOfWave = oldValue;
            return true;
        } else if (!lastStatus && isDirectionUp) {
            valleyOfWave = oldValue;
            return false;
        } else {
            return false;
        }
    }

    /*
     * 阈值的计算
     * 1.通过波峰波谷的差值计算阈值
     * 2.记录4个值，存入tempValue[]数组中
     * 3.在将数组传入函数averageValue中计算阈值
     * */
    public float peakValleyThread(float value) {
        float tempThread = ThreadValue;
        if (tempCount < ValueNum) {
            tempValue[tempCount] = value;
            tempCount++;
        } else {
            tempThread = averageValue(tempValue, ValueNum);
            for (int i = 1; i < ValueNum; i++) {
                tempValue[i - 1] = tempValue[i];
            }
            tempValue[ValueNum - 1] = value;
        }
        return tempThread;

    }

    /*
     * 梯度化阈值
     * 1.计算数组的均值
     * 2.通过均值将阈值梯度化在一个范围里
     * */
    public float averageValue(float[] value, int n) {
        float ave = 0;
        for (int i = 0; i < n; i++) {
            ave += value[i];
        }
        ave = ave / ValueNum;
        if (ave >= 8)
            ave = (float) 4.3;
        else if (ave >= 7 && ave < 8)
            ave = (float) 3.3;
        else if (ave >= 4 && ave < 7)
            ave = (float) 2.3;
        else if (ave >= 3 && ave < 4)
            ave = (float) 2.0;
        else {
            ave = (float) 1.3;
        }
        return ave;
    }
    //--------------------------------------

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (sensorManager!=null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}
