package com.codex.talkntrace;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class Image_preview extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Intent i = getIntent();
        String url = i.getStringExtra("url");
        Toast.makeText(Image_preview.this,url,Toast.LENGTH_SHORT).show();
        imageView = (ImageView)findViewById(R.id.image_view_activty);
        Picasso.with(this).load(url).into(imageView);

    }
}
