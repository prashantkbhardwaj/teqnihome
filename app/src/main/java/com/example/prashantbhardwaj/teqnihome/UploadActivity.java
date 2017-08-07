package com.example.prashantbhardwaj.teqnihome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btSelectPicture;
    private Button btUpload;
    private ImageView imageView;
    private Spinner spSessionName;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL ="http://192.168.1.101/fileTransfers/teqniHome/upload.php";
    private String KEY_IMAGE = "image";
    private String KEY_FOLDER = "folder";
    private String KEY_UPLOADER = "uploader";
    private String KEY_BRANCH = "branch";
    private String KEY_YEAR = "year";
    private String KEY_SESSIONNAME = "sessionName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btSelectPicture = (Button) findViewById(R.id.btSelectPicture);
        btUpload = (Button) findViewById(R.id.btUpload);

        spSessionName = (Spinner) findViewById((R.id.spSession));
        spSessionName.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        imageView  = (ImageView) findViewById(R.id.imageView);

        btSelectPicture.setOnClickListener(this);
        btUpload.setOnClickListener(this);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                        UploadActivity.this.startActivity(intent);
                        Toast.makeText(UploadActivity.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(UploadActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name

                final SessionManagement session;
                session = new SessionManagement(getApplicationContext());
                session.checkLogin();

                HashMap<String, String> user = session.getUserDetails();
                String uploader = user.get(SessionManagement.KEY_USERNAME);
                String folderTxt = user.get(SessionManagement.KEY_BRANCH).toString()+"/"+user.get(SessionManagement.KEY_YEAR).toString()+"/"+String.valueOf(spSessionName.getSelectedItem());
                System.out.println(folderTxt);
                String folder = folderTxt;
                String branch = user.get(SessionManagement.KEY_BRANCH);
                String year = user.get(SessionManagement.KEY_YEAR);

                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_UPLOADER, uploader);
                params.put(KEY_FOLDER, folder);
                params.put(KEY_BRANCH, branch);
                params.put(KEY_YEAR, year);
                params.put(KEY_SESSIONNAME, String.valueOf(spSessionName.getSelectedItem()));
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == btSelectPicture){
            showFileChooser();
        }

        if(v == btUpload){
            uploadImage();
        }
    }
}
