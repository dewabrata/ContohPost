package com.juaracoding.contohpost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.filter.Filters;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.jrizani.jrdatetimepicker.DateTimePicker;

public class PostActivity extends AppCompatActivity {

    EditText txtDari,txtTime,txtText;
    ImageView imgHasil;
    CameraView camera;
    Button btnCapture, btnSend, btnGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        txtDari = findViewById(R.id.txtDari);
        txtTime =  findViewById(R.id.txtTime);
        txtText = findViewById(R.id.txtText);
        imgHasil = findViewById(R.id.imageView);
        btnCapture = findViewById(R.id.btnCapture);
        btnSend = findViewById(R.id.btnKirim);
        btnGallery = findViewById(R.id.btnGallery);
        final DateTimePicker.Builder builder = new DateTimePicker.Builder(this, R.style.Theme_AppCompat);

        Calendar date = Calendar.getInstance();
        builder.setInitialDateTime(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE),
                true
        );
        builder.setOnDateTimeChangeListener(new DateTimePicker.OnDateTimeSelectListener() {
            @Override
            public void onDateTimeSelected(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                txtTime.setText(formatter.format(selectedDate.getTime()));
            }
        });

        final DateTimePicker dialog = builder.create();

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.setFilter(Filters.BLACK_AND_WHITE.newInstance());

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                // Picture was taken!
                // If planning to show a Bitmap, we will take care of
                // EXIF rotation and background threading for you...
                result.toBitmap(300, 300, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {

                        imgHasil.setImageBitmap(bitmap);

                    }
                });

                // If planning to save a file on a background thread,
                // just use toFile. Ensure you have permissions.
                //result.toFile(file, callback);

                // Access the raw data if needed.
                byte[] data = result.getData();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture();
            }
        });



        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        final FilePickerDialog dialogPicker = new FilePickerDialog(PostActivity.this,properties);
        dialogPicker.setTitle("Pilih file csv");

        dialogPicker.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.

                Picasso.get().load("file://" +files[0]).into(imgHasil);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogPicker.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }


}
