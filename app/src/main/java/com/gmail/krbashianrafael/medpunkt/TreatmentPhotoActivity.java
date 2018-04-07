package com.gmail.krbashianrafael.medpunkt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TreatmentPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_photo);

        /*Intent intent = getIntent();
        Uri data = intent.getData();

        TextView TextView = findViewById(R.id.m_textView);


        String fullDataString = data.toString();

        int indexOfstart = fullDataString.indexOf("+");

        String photoUri = fullDataString.substring(indexOfstart + 1, fullDataString.length());

        ImageView imageView = findViewById(R.id.img);

        TextView.setText("photoUri: " + photoUri);

        File imgFile = new File(photoUri);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }*/

    }
}
