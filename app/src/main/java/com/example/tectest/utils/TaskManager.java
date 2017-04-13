package com.example.tectest.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by ymontero on 12/04/2017.
 */

public class TaskManager {
    public static void openSettings(Activity activity){
        try {

            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            //e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            activity.startActivity(intent);
        }
    }
}
