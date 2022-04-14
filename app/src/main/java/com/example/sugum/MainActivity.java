package com.example.sugum;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    private boolean diabetesLogic(int red, int green, int blue){

        double avg = (red+green+blue) / 3;
        double red_avg = red / avg;
        double blue_avg = blue / avg;
        double green_avg = green / avg;

        double threshold_min = 0.92;
        double threshold_max = 1.1;

        if((red_avg > threshold_min && red_avg < threshold_max) && (blue_avg > threshold_min && blue_avg < threshold_max) && (green_avg > threshold_min && green_avg < threshold_max)){
            return true;
        }
        else {
            return false;
        }
    }

    private int approximateToNearestTen(int rgbval){
        if ((rgbval % 10) > 5) {
            return rgbval + (10 - (rgbval % 10));
        } else {
            return rgbval - (rgbval % 10);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            ImageView image_view = (ImageView) findViewById(R.id.imageView);
            if (requestCode == 10  && resultCode  == RESULT_OK) {

                final Uri imageUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image_view.setImageBitmap(selectedImage);

                int x = selectedImage.getWidth();
                int y = selectedImage.getHeight();
                int red = 0;
                int blue = 0;
                int green = 0;
                int pixel;
                for(int i = 0; i < x-1; i++){
                    for(int j = 0; j<y-1; j++){
                        pixel = selectedImage.getPixel(i,j);
                        red += Color.red(pixel);
                        blue += Color.blue(pixel);
                        green += Color.green(pixel);
                    }
                }
                red = approximateToNearestTen(red/(x*y));
                blue = approximateToNearestTen(blue/(x*y));
                green = approximateToNearestTen(green/(x*y));
                boolean result = diabetesLogic(red, green, blue);
                if(result == true){
                    noprob();
                } else {
                    prob();
                }
            }

    }

   public void prob() {
        Intent intentx = new Intent(this, problemo.class);
        MainActivity.this.startActivity(intentx);
    }

    private void noprob() {
        Intent intentx = new Intent(this, noproblemo.class);
        MainActivity.this.startActivity(intentx);
    }


    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button1 = findViewById(R.id.imageselect);
        button1.setOnClickListener(view -> {
            final int RESULT_LOAD_IMG = 10;
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); // photopicker = action_pick
            photoPickerIntent.setType("image/*");
            try {
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Could not start camera.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}