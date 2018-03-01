package com.codex.talkntrace;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ADMIN on 28-03-2017.
 */

class CheckNetConnection {


    Context context;

    public CheckNetConnection(Context context)
    {
        this.context = context;
    }

    public boolean isConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info!=null)
            {
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }


}
