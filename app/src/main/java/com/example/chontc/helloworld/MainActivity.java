package com.example.chontc.helloworld;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import com.jjoe64.graphview.GraphView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity implements SerialInputOutputManager.Listener {

    private int counter = 10;
    GraphView graphViewTemp;
    private static final String ACTION_USB_PERMISSION = "com.android.recipes.USB_PERMISSION";
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    UsbSerialPort port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timer aTimer = new Timer();

        TimerTask aTask = new TimerTask() {
            @Override
            public void run(){
                sendDataToThingSpeak("1", counter + "");
                counter++;
                if (counter >= 20) counter = 10;
            }
        };

//        aTimer.schedule(aTask, 10000, 60000);

//        graphViewTemp = findViewById(R.id.graphTemperature);

//        DataPoint[] dataPointTemp = new DataPoint[10];
//        dataPointTemp[0] = new DataPoint(0, 30);
//        dataPointTemp[1] = new DataPoint(1, 31);
//        dataPointTemp[2] = new DataPoint(2, 31);
//        dataPointTemp[3] = new DataPoint(3, 29);
//        dataPointTemp[4] = new DataPoint(4, 20);
//        dataPointTemp[5] = new DataPoint(5, 21);
//        dataPointTemp[6] = new DataPoint(6, 21);
//        dataPointTemp[7] = new DataPoint(7, 25);
//        dataPointTemp[8] = new DataPoint(8, 27);
//        dataPointTemp[9] = new DataPoint(9, 35);
//
//        LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<>(dataPointTemp);
//
//        showDataOnGraph(seriesTemp, graphViewTemp);

//        setupBlinkyTimer();

        openUART("TEST");

        //Text sang giọng nói
        tts=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.getDefault());
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                            Log.e("error", "This Language is not supported");
                    }
                    else{
                        ConvertTextToSpeech("Hello");
                        Log.d("ABC", "OKOK");
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });
        //end text sang giọng nói
        ConvertTextToSpeech("Hello Johnny!");
    }

    private void sendDataToThingSpeak(String id, String value){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String apiURL = "https://api.thingspeak.com/update?api_key=HQ5KL72BNLKBMNA3&field" + id + "=" + value;

        Request request = builder.url(apiURL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }

    private void getDataToThingSpeak(int so_luong){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String apiURL = "https://api.thingspeak.com/channels/1281568/feeds.json?results=" + so_luong;

        Request request = builder.url(apiURL).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonString = response.body().string();
                try{
                    JSONObject jsonData = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonData.getJSONArray("feeds");
                    double temp0 = jsonArray.getJSONObject(0).getDouble("field1");
                    double temp1 = jsonArray.getJSONObject(1).getDouble("field1");
                    double temp2 = jsonArray.getJSONObject(2).getDouble("field1");
                    double temp3 = jsonArray.getJSONObject(3).getDouble("field1");
                    double temp4 = jsonArray.getJSONObject(4).getDouble("field1");
                    double temp5 = jsonArray.getJSONObject(5).getDouble("field1");
                    double temp6 = jsonArray.getJSONObject(6).getDouble("field1");
                    double temp7 = jsonArray.getJSONObject(7).getDouble("field1");
                    double temp8 = jsonArray.getJSONObject(8).getDouble("field1");
                    double temp9 = jsonArray.getJSONObject(9).getDouble("field1");
                    Log.d("ABC", jsonString);
                    LineGraphSeries<DataPoint> seriesTemp = new LineGraphSeries<>(new DataPoint[]
                        {   new DataPoint(0, temp0),
                                new DataPoint(1, temp1),
                                new DataPoint(2, temp2),
                                new DataPoint(3, temp3),
                                new DataPoint(4, temp4),
                                new DataPoint(5, temp5),
                                new DataPoint(6, temp6),
                                new DataPoint(7, temp7),
                                new DataPoint(8, temp8),
                                new DataPoint(9, temp9)
                        });

                    graphViewTemp = findViewById(R.id.graphTemperature);
                    showDataOnGraph(seriesTemp, graphViewTemp);
                }catch (Exception e){}
            }
        });
    }

    private void openUART(String message){
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            Log.d("UART", "UART is not available");

        }else {
            Log.d("UART", "UART is available");

            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {

                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);
                manager.requestPermission(driver.getDevice(), PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));

                return;
            } else {

                port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    port.write((message+"#").getBytes(), 1000);
                    SerialInputOutputManager usbIoManager = new SerialInputOutputManager(port, this);
                    Executors.newSingleThreadExecutor().submit(usbIoManager);
                } catch (Exception e) {

                }
            }
        }
    }

    /** Called when the user touches the button */
    public void sendMessageBtn1(View view) {openUART("1");}

    public void sendMessageBtn2(View view) {
        openUART("2");
    }

    public void sendMessageBtn3(View view) {
        openUART("3");
    }

    public void sendMessageBtn4(View view) {
        openUART("4");
    }

    private void showDataOnGraph(LineGraphSeries<DataPoint> series, GraphView graph){
        if(graph.getSeries().size() > 0){
            graph.getSeries().remove(0);
        }
        graph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
    }

    private void setupBlinkyTimer(){
        Timer mTimer = new Timer();
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                getDataToThingSpeak(10);
            }
        };
        mTimer.schedule(mTask, 2000, 5000);
    }

    final int REQ_CODE_SPEECH_INPUT = 100;

    private void promptSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Đang lắng nghe...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Không hỗ trợ giọng nói", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Log.d("VOICE", "***" + result.get(0) + "***");
                    String cmd = result.get(0).toLowerCase();
                    if (cmd.contains("bật") && cmd.contains("đèn")){
                        try{
                            port.write("1#".getBytes(), 1000);
                        }
                        catch (Exception e){

                        }
                    }

//                    EditText input ((EditText)findViewById(R.id.editTextTaskDescription));
//                    input.setText(result.get(0)); // set the input data to the editText alongside if want to.
//                    result.get(0) chính xác 90%
//                    result.get(1)
//                    result.get(2)
                }
                break;
            }

        }
    }

    String buffer = "";
    @Override
    public void onNewData(byte[] data) {
        buffer += new String(data);
        TextView myAwesomeTextView = (TextView)findViewById(R.id.txtMessage);


        int SoC = buffer.indexOf("!");
        int EoC = buffer.indexOf("#");

        if (SoC >= 0 && EoC > SoC){
            String cmd = buffer.substring(SoC + 1, EoC);
            myAwesomeTextView.setText(cmd);
            buffer = buffer.substring(EoC + 1, buffer.length());
        }
    }

    @Override
    public void onRunError(Exception e) {

    }

    public void onReadTextClick(View v){
        EditText et=(EditText)findViewById(R.id.editSpeech);
        ConvertTextToSpeech(et.getText().toString());
    }

    TextToSpeech tts;
    private void ConvertTextToSpeech(String data) {
        tts.speak(data, TextToSpeech.QUEUE_FLUSH, null);
    }
}
