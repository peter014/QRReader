package com.example.ceaiot.qrreader;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scanner ;
    private static Socket s;
    private static PrintWriter printWriter;

    String ipReg = "";
    String ipDem = "";
    String ip = "";
    int puerto = 5000;
    private String message = "";
    private static final String IPADDRESS_PATTERN ="^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    protected void onPause() {
        super.onPause();
        if(scanner != null){
            scanner.stopCamera();
        }

    }

    public void onClickScan(View view){
        scanner = new ZXingScannerView(this);
        setContentView(scanner);
        scanner.setResultHandler(this);
        scanner.startCamera();
    }

    public void onClickEnviarRegistro(View view){
        TextView textView = (TextView) findViewById(R.id.ipRegistro);
        String s = textView.getText().toString();
        if(s.matches(IPADDRESS_PATTERN)){
            ip = s;
            ipReg = s;
            puerto = 5000;
            sendMessage();
        }
        else{
            Toast.makeText(getApplicationContext(),"IP Registro incorrecta",Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickEnviarDemo(View view){
        TextView textView = (TextView) findViewById(R.id.ipDemo);
        String s = (String) textView.getText().toString();
        if(s.matches(IPADDRESS_PATTERN)){
            ip = s;
            ipDem = s;
            puerto = 5001;
            sendMessage();
        }
        else{
            Toast.makeText(getApplicationContext(),"IP Demo incorrecta",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (scanner != null && scanner.isEnabled()) {
            setContentView(R.layout.activity_main);
            scanner.stopCamera();
            scanner = null;
            EditText t = (EditText) findViewById(R.id.ipDemo);
            t.setText(ipDem);

            EditText te = (EditText) findViewById(R.id.ipRegistro);
            te.setText(ipReg);
        }
        else{
            this.finish();
        }
    }

    private void sendMessage(){
        myTask mt = new myTask();
        mt.execute();
    }

    class myTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                s = new Socket(ip,puerto);
                printWriter = new PrintWriter(s.getOutputStream());
                printWriter.write(message);
                printWriter.flush();
                printWriter.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void handleResult(Result result) {
        //
        scanner.resumeCameraPreview(this);
        scanner.stopCamera();
        scanner = null;
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.result);
        message  = result.getText();
        textView.setText("Correo: " + message);

        EditText t = (EditText) findViewById(R.id.ipDemo);
        t.setText(ipDem);

        EditText te = (EditText) findViewById(R.id.ipRegistro);
        te.setText(ipReg);

    }
}