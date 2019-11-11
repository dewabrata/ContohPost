package com.juaracoding.contohpost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.juaracoding.contohpost.APIService.APIClient;
import com.juaracoding.contohpost.APIService.APIInterfacesRest;
import com.juaracoding.contohpost.APIService.AppUtil;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.filter.Filters;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.jrizani.jrdatetimepicker.DateTimePicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    EditText txtDari, txtTime, txtText;
    ImageView imgHasil;
    CameraView camera;
    Button btnCapture, btnSend, btnGallery;
    Bitmap gambarnya;


    //untuk dialog progress

    LottieAlertDialog progressDialog;

    public void setProgressDialog() {
        progressDialog = new LottieAlertDialog.Builder(PostActivity.this, DialogTypes.TYPE_LOADING)
                .setTitle("Loading")

                .setDescription("Please Wait")
                .build();
        progressDialog.setCancelable(false);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        txtDari = findViewById(R.id.txtDari);
        txtTime = findViewById(R.id.txtTime);
        txtText = findViewById(R.id.txtText);
        imgHasil = findViewById(R.id.imageView);
        btnCapture = findViewById(R.id.btnCapture);
        btnSend = findViewById(R.id.btnKirim);
        btnGallery = findViewById(R.id.btnGallery);

        //add progress dialog
        setProgressDialog();

        //add datetimepicker
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


        //add camera view
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
                        gambarnya = bitmap;

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


        //add file picker
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        final FilePickerDialog dialogPicker = new FilePickerDialog(PostActivity.this, properties);
        dialogPicker.setTitle("Pilih file csv");

        dialogPicker.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.


                Picasso.get().load("file://" + files[0]).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imgHasil.setImageBitmap(bitmap);
                        gambarnya = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(PostActivity.this, "Maaf gambar gagal diload", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogPicker.show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gambarnya!=null){
                    sendData(gambarnya);
                }else{
                    Toast.makeText(PostActivity.this, "Maaf koneksi bermasalah", Toast.LENGTH_LONG).show();
                }

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


    APIInterfacesRest apiInterface;


    //send post data with image
    private void sendData(Bitmap bitmap) {


        progressDialog.show();


        File file = createTempFile(bitmap);
        byte[] bImg1 = AppUtil.FiletoByteArray(file);
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/jpeg"),bImg1);
      //  RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/jpeg"), compressCapture(bImg1, 900));
        MultipartBody.Part bodyImg1 =
                MultipartBody.Part.createFormData("gambar", file.getName() + ".jpg", requestFile1);


        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);


        Call<ModelAdd> postAdd = apiInterface.sendTextJalan(

                toRequestBody(AppUtil.replaceNull(txtDari.getText().toString())),
                toRequestBody(AppUtil.replaceNull(txtTime.getText().toString())),
                toRequestBody(AppUtil.replaceNull(txtText.getText().toString())),
                toRequestBody(AppUtil.replaceNull("OK")),
                bodyImg1);

        postAdd.enqueue(new Callback<ModelAdd>() {
            @Override
            public void onResponse(Call<ModelAdd> call, Response<ModelAdd> response) {

                progressDialog.dismiss();

                ModelAdd responServer = response.body();

                if (responServer != null) {

                    Toast.makeText(PostActivity.this,responServer.getMessage(),Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<ModelAdd> call, Throwable t) {

                progressDialog.show();
                Toast.makeText(PostActivity.this, "Maaf koneksi bermasalah", Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });

    }


    //change string to requestbody
    public RequestBody toRequestBody(String value) {
        if (value == null) {
            value = "";
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }


    //compress picture
    public static byte[] compressCapture(byte[] capture, int maxSizeKB) {

        // This should be different based on the original capture size
        int compression = 12;

        Bitmap bitmap = BitmapFactory.decodeByteArray(capture, 0, capture.length);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, outputStream);
        return outputStream.toByteArray();
    }

    //bitmap to file
    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + "");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


}
