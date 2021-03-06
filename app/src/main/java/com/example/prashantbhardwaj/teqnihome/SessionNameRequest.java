package com.example.prashantbhardwaj.teqnihome;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prashantbhardwaj on 04/08/17.
 */

public class SessionNameRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://192.168.1.101/fileTransfers/teqniHome/getData.php";
    private Map<String, String> params;

    public SessionNameRequest(String sessionName, String branch, String year,Response.Listener<String> listener){
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("sessionName", sessionName);
        params.put("branch", branch);
        params.put("year", year);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

