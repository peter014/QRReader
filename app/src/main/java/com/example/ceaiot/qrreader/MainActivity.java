package com.example.ceaiot.qrreader;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private String message = "";
    private static String ip = "10.151.21.249";


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

    @Override
    public void onBackPressed() {
        if (scanner != null && scanner.isEnabled()) {
            setContentView(R.layout.activity_main);
            scanner.stopCamera();
            scanner = null;
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
                s = new Socket(ip,5000);
                printWriter = new PrintWriter(s.getOutputStream());
                printWriter.write(message);
                printWriter.flush();
                printWriter.close();
                s.close();


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Error en el envío",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(),"Error en el envío",Toast.LENGTH_SHORT).show();
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
        textView.setText("Cedula: "+result.getText());
        message  = result.getText();
        sendMessage();
    }
}
