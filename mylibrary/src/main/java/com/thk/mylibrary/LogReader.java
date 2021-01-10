package com.thk.mylibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogReader extends Thread {

    private Process process;
    private BufferedReader bufferedReader;
    private LogListener logListener;
    private boolean read = true;

    public interface LogListener{
        void onLogRead(String msg);
    }

    public void stopReading() {
        this.read = false;
    }

    public void continueReading() {
        this.read = true;
    }

    @Override
    public void run() {
        super.run();
        try{
            String command = "logcat --pid="+android.os.Process.myPid()+" -b main";
            process = Runtime.getRuntime().exec(command);
        }catch (IOException e){
            e.printStackTrace();
        }
        readLog();
    }

    private void readLog(){
        BufferedReader br = getBufferedReader();
        String line = "";
        try {
            while ((line = br.readLine()) != null && read) {
                if(logListener != null){
                    logListener.onLogRead(line);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private BufferedReader getBufferedReader(){
        if(bufferedReader == null && process != null){
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        }
        return bufferedReader;
    }

    public void setLogListener(LogListener logListener) {
        this.logListener = logListener;
    }

    public LogListener getLogListener() {
        return logListener;
    }
}
