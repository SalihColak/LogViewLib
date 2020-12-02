package com.thk.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private String filterTag="";
    private String filterOptions = "";

    private Boolean readLogs = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);

       Log.v("test","halllllllo");

    }

    public void filter(View view) {
        readLogs = true;
        filterTag = editText.getEditableText().toString();
        if(!filterTag.equals("")){
            filterOptions = ":V *:S";
        }else{
            filterOptions = "";
        }
    }

    private class MyTask extends AsyncTask<Void,StringBuilder,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            while(true) {
                if (readLogs) {
                    try {

                        int count = 0;
                        int pid = android.os.Process.myPid();
                        String command = "logcat --pid=" + pid + " -b main -v tag "+filterTag+filterOptions;
                        Process process = Runtime.getRuntime().exec(command);
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        StringBuilder log = new StringBuilder();
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {
                            log.append(++count + ") " + line + "\n");
                            publishProgress(log);

                        }

                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(StringBuilder... values) {
            super.onProgressUpdate(values);

            textView.invalidate();
            textView.setText(values[0].toString());
            textView.setMovementMethod(new ScrollingMovementMethod());

        }
    }

    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable() {
        @Override
        public void run() {

            textView.invalidate();


           /* try {
                int count=0;
                int pid = android.os.Process.myPid();
                String command = "logcat --pid="+pid+" -d -v tag";
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                StringBuilder log=new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(++count +") "+line +"\n");
                }
                textView.setText(log.toString());
                textView.setMovementMethod(new ScrollingMovementMethod());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            Log.v("brt","brrrtttt");*/

            viewHandler.postDelayed(this, 1000);
        }
    };

}
