package com.example.chontc.helloworld;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import com.jjoe64.graphview.GraphView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

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

//        openUART();
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
//                    SerialInputOutputManager usbIoManager = new SerialInputOutputManager(port, this);
//                    Executors.newSingleThreadExecutor().submit(usbIoManager);
                } catch (Exception e) {

                }
            }
        }
    }

    /** Called when the user touches the button */
    public void sendMessageBtn1(View view) {
        openUART("1");
    }

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

}
