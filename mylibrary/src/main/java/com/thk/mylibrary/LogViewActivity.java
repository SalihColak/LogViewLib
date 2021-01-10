package com.thk.mylibrary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LogViewActivity extends AppCompatActivity {

    private String filterTag="";
    private String filterOptions = "";
    private Boolean readLogs = true;

    private TextView textView;
    private MyTask myTask;

    private Process process;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logview);
        textView = findViewById(R.id.logs);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText("");

        /*final ScrollView scrollView = findViewById(R.id.myscrollview);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        myTask = new MyTask();
        myTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        process.destroy();
    }

    /*public void filter(View view) {
        readLogs = true;
        filterTag = editText.getEditableText().toString();
        if(!filterTag.equals("")){
            filterOptions = ":V *:S";
        }else{
            filterOptions = "";
        }
    }*/

    private class MyTask extends AsyncTask<Void,String,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
                    try {
                        int count = 0;
                        int pid = android.os.Process.myPid();
                        String command = "logcat --pid=" + pid + " -b main"+filterTag+filterOptions;
                        process = Runtime.getRuntime().exec(command);
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        //StringBuilder log = new StringBuilder();
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {
                            //Thread.sleep(100);
                            //log.append(++count + ") " + line + "\n");
                            publishProgress(line);
                        }


                    } catch (Exception e) {
                        //e.printStackTrace();
                    }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String output = values[0]+"\n";
            textView.append(output);

        }
    }
}
