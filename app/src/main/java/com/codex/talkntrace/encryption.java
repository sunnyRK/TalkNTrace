package com.codex.talkntrace;

import android.util.Log;

/**
 * Created by Rutviz Vyas on 01/04/2017.
 */

public class encryption {

    static String encrypt(String abc)
    {

        String res = "";
        int len = abc.length();
        for(char c:abc.toCharArray())
        {
            c=(char) (c+len);
            res+=c+"";
        }
            Log.d("Encrypt","Encrypt "+res);
        return res;
    }
    static String dcrypt(String abc)
    {

        String res = "";
        int len = abc.length();
        for(char c:abc.toCharArray())
        {
            c=(char) (c-len);
            res+=c+"";
        }
        Log.d("Encrypt","Dcrypt "+res);
        return res;
    }
}
