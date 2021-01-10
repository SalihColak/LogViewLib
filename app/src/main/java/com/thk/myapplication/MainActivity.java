package com.thk.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.thk.mylibrary.LogViewer;

public class MainActivity extends AppCompatActivity{

    LogViewer logViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("test","Dies ist ein Test-Log");
        logViewer = new LogViewer(this); //Jede Activtiy der die Funktionen von LogViewer benötigt muss eine eigene Instanz von LogViewer erstellen
    }



    /**
     * Möglichkeit 1 für die Einbindung der Library:
     * Als Icon in der Action Bar, der die LogViewActivity startet oder das PopupWindow erstellt.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logactivity:
                logViewer.startLogViewActivity();
                break;
            case R.id.logpopup:
                logViewer.startPopupView();
                break;
            case R.id.logtoast:
                logViewer.registerLogAsToast();
                break;
            case R.id.lifecycle:
                logViewer.trackActivityLifecycle(this);
                break;
            default: return super.onOptionsItemSelected(item);
        }
        return false;
    }

    /**
     * Logs als Toast-Nachrichten ausgeeben abmelden.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        logViewer.unregisterLogAsToast();
    }

    /**
     * Möglichkeit 2 für die Einbindung der Library:
     * Der Programmierer entscheidet selbst, wie er die LogViewActivity startet.
     */

    public void initLogViewer(){
        logViewer.startLogViewActivity();
        logViewer.startPopupView();
        logViewer.trackActivityLifecycle(this);
        logViewer.registerLogAsToast();
    }

    // -------------------------------------------------------------------------------
}
