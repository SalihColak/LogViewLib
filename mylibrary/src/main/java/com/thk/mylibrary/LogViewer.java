package com.thk.mylibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;



public class LogViewer{

    private Activity activity;

    private LogReader logReader;
    private MainThread mainThread;

    private boolean popUpLoaded = false;
    private String filterTag = "-";
    private PopupWindow popupWindow;
    private int popUpX;
    private int popUpY;

    private MyObserver myObserver;

    public LogViewer(@NonNull Activity activity){
        this.mainThread = new LogMainThread();
        this.logReader = new LogReader();
        this.activity = activity;
        myObserver = new MyObserver(activity);
    }

    private boolean lifecycleRegistered = false;

    public void trackActivityLifecycle(@NonNull LifecycleOwner lifecycleOwner){
        final Context context = activity.getApplicationContext();
        if(!lifecycleRegistered){
            lifecycleOwner.getLifecycle().addObserver(myObserver);
            Toast.makeText(context,"Lifecycle Tracking ist jetzt aktiviert",Toast.LENGTH_SHORT).show();
            lifecycleRegistered = !lifecycleRegistered;
        }else{
            lifecycleOwner.getLifecycle().removeObserver(myObserver);
            Toast.makeText(context,"Lifecycle Tracking ist jetzt deaktiviert",Toast.LENGTH_SHORT).show();
            lifecycleRegistered = !lifecycleRegistered;
        }
    }

    public void startLogViewActivity(){
        Intent intent = new Intent(activity, LogViewActivity.class);
        activity.startActivity(intent);
    }

    public void startPopupView(){

        if(!popUpLoaded){
            if(popupWindow == null) {
                LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

                final View popupView = inflater.inflate(R.layout.popup_window, null);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels / 4;
                int width = (int) (displayMetrics.widthPixels / 1.3);

                popupWindow = new PopupWindow(popupView, width, height, false);

                popupWindow.showAtLocation(viewGroup, Gravity.CENTER, 0, 0);

                ImageButton imageButton = popupView.findViewById(R.id.closeButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        popUpLoaded = false;
                    }
                });
                popUpLoaded = true;

                popupView.setOnTouchListener(new View.OnTouchListener() {
                    private float mDx;
                    private float mDy;
                    private int mCurrentX = (int) popupView.getX();
                    private int mCurrentY = (int) popupView.getY();

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                mDx = mCurrentX - event.getRawX();
                                mDy = mCurrentY - event.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                mCurrentX = (int) (event.getRawX() + mDx);
                                mCurrentY = (int) (event.getRawY() + mDy);
                                popUpX = mCurrentX;
                                popUpY = mCurrentY;
                                popupWindow.update(mCurrentX, mCurrentY, -1, -1);
                                break;
                        }
                        return true;
                    }
                });

                logReader.setLogListener(new LogReader.LogListener() {
                    @Override
                    public void onLogRead(final String msg) {
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = popupView.findViewById(R.id.textest);
                                textView.append(msg + "\n");
                            }
                        });

                    }
                });
                if (logReader.getState().equals(Thread.State.NEW)) {
                    logReader.start();
                }
            }else {
                ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();
                popupWindow.showAtLocation(viewGroup,Gravity.CENTER,popUpX,popUpY);
            }
        } // end if
    }//end method

    public void registerLogAsToast(){

        //final Context context = activity.getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Tag Filter:");
        final EditText input = new EditText(activity);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterTag = input.getText().toString();
                if (filterTag.equals("")){
                    Toast.makeText(activity,"Sie müssen einen Tag Filter anlegen, um diese Funktion zu nutzen.",Toast.LENGTH_LONG).show();
                    return;
                }

                logReader.setLogListener(new LogReader.LogListener() {
                    @Override
                    public void onLogRead(final String msg) {

                        String[] splittedMsg = msg.split(":");
                        if(splittedMsg.length>=3){
                            final Log log = new Log();
                            String message = "";
                            for (int i = 3; i< splittedMsg.length; i++){
                                if(i == splittedMsg.length-1){
                                    message += splittedMsg[i];
                                }else{
                                    message += splittedMsg[i] +":";
                                }

                            }
                            log.setMessage(message);
                            String metaDataRaw = splittedMsg[0]+":"+splittedMsg[1]+ ":"+splittedMsg[2];
                            String[] metaData = metaDataRaw.split(" ");
                            List<String> metaDataList = new ArrayList<>();
                            for (int k=0; k<metaData.length; k++){
                                if(!metaData[k].equals("")){
                                    metaDataList.add(metaData[k]);
                                }
                            }
                            metaData = metaDataList.toArray(new String[0]);
                            if(metaData.length == 6){
                                log.setTime(metaData[0]+" "+metaData[1]);
                                log.setPid(Integer.parseInt(metaData[2]));
                                log.setTid(Integer.parseInt(metaData[3]));
                                log.setLevel(metaData[4]);
                                log.setTag(metaData[5]);
                                mainThread.post(new Runnable() {
                                    @Override
                                    public void run() {
                                     if(log.getTag().equals(filterTag)) {
                                         Toast.makeText(activity, log.getTag() + ":" + log.getMessage(), Toast.LENGTH_SHORT).show();
                                     }
                                    }
                                });

                            }
                        }


                    }
                });

                if(logReader.getState().equals(Thread.State.NEW)){
                    logReader.start();
                }
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void unregisterLogAsToast(){
        logReader.stopReading();
    }


}
