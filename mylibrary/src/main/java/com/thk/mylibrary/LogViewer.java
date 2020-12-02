package com.thk.mylibrary;

import android.content.Context;
import android.content.Intent;

public class LogViewer {

    public static void initialze(Context context){

        Intent intent = new Intent(context, LogViewActivity.class);
        context.startActivity(intent);
    }

}
