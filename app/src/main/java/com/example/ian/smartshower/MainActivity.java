package com.example.ian.smartshower;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileOutputStream;

public class MainActivity extends ActionBarActivity {

    // UI Elements
    ToggleButton buttonTempToggle;
    Button buttonTempInc;
    Button buttonTempDec;
    EditText fieldTempIncrement;
    TextView changeToTemp;
    TextView currentUnit;

    // Data
    char tempUnit;
    int tempChange;
    double totalChangeFarenheit=90;

    // Timer
    private long startTime=2*1000; // 2 SECONDS IDLE TIME
    private final long interval = 1 * 1000;
    MyCountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up buttons/fields
        buttonTempToggle = (ToggleButton)findViewById(R.id.temptoggle);
        buttonTempInc = (Button)findViewById(R.id.incTemp);
        buttonTempDec = (Button)findViewById(R.id.decTemp);
        fieldTempIncrement = (EditText)findViewById(R.id.tempInput);
        changeToTemp = (TextView)findViewById(R.id.changeToTemp);
        currentUnit = (TextView)findViewById(R.id.currentUnit);

        // Find unit of temperature
        if(buttonTempToggle.isChecked())
            tempUnit = 'C';
        else
            tempUnit = 'F';

        // Set Add/Subtract button text
        buttonTempInc.setText(R.string.tempIncreaseF);
        buttonTempDec.setText(R.string.tempDecreaseF);

        // Detect increment user will change temp by
        tempChange = Integer.parseInt(fieldTempIncrement.getText().toString());

        // Detect idle
        countDownTimer = new MyCountDownTimer(startTime, interval);
        countDownTimer.start();

        // Save changeTo temp to file
        String filename = "requesttemp";
        String string = Double.toString(totalChangeFarenheit);
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeUnit(View v) {
        if (buttonTempToggle.isChecked()) { // ON -> Celsius
            tempUnit = 'C';
            // Update UI elements
            currentUnit.setText(R.string.showUnitC);
            buttonTempInc.setText(R.string.tempIncreaseC);
            buttonTempDec.setText(R.string.tempDecreaseC);
            changeToTemp.setText(Double.toString(
                            (double)Math.round((totalChangeFarenheit-32)*5/9*10)/10)
            );
        }else{                              // OFF -> Farenheit
            tempUnit = 'F';
            // Update UI elements
            currentUnit.setText(R.string.showUnitF);
            buttonTempInc.setText(R.string.tempIncreaseF);
            buttonTempDec.setText(R.string.tempDecreaseF);
            changeToTemp.setText(Double.toString(
                    (double)Math.round(totalChangeFarenheit*10)/10)
            );
        }
    }

    public void onClick(View v){
        // Detect increase/decrease in temperature
        tempChange = Integer.parseInt(fieldTempIncrement.getText().toString());

        // Add amount to requested temperature
        if(v.getId()==R.id.incTemp) {
            if (tempUnit == 'F') // Farenheit
                totalChangeFarenheit += tempChange;
            else                 // Celsius
                totalChangeFarenheit += tempChange * 1.8;

        // Subtract amount from requested temperature
        }else if (v.getId()==R.id.decTemp) {
            if (tempUnit == 'F') // Farenheit
                totalChangeFarenheit -= tempChange;
            else                 // Celsius
                totalChangeFarenheit -= tempChange * 1.8;
        }
        updateChange(v);
    }

    public void updateChange(View v){
        // Display temperature in Farenheit
        if(tempUnit=='F')
            changeToTemp.setText(Double.toString(
                    (double)Math.round(totalChangeFarenheit*10)/10)
            );
        // Display temperature in Celsius
        else
            changeToTemp.setText(Double.toString(
                    (double)Math.round((totalChangeFarenheit-32)*5/9*10)/10)
            );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserInteraction(){

        super.onUserInteraction();

        // Reset the timer
        countDownTimer.cancel();
        countDownTimer.start();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {

            // Save changeTo temp to file
            String filename = "requesttemp";
            String string = Double.toString(totalChangeFarenheit);
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Send a toast
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
}