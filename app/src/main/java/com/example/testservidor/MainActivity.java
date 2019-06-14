package com.example.testservidor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import id.zelory.compressor.*;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE = 12;
    private static final int CAPTURA_FOTO = 13;

    private ImageView mPhotoImageView;
    private Context mContext;
    Uri uriImagen = null;
    Uri fotoURI;
    File imagen;
    byte[] byteArray;
    Button btnCamara, btnDenunciar, btnGaleria;
    private TextureView destinoCamara;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_foto).setOnClickListener(this);
        findViewById(R.id.btn_denunciar).setOnClickListener(this);
        findViewById(R.id.btnGaleria).setOnClickListener(this);
        mPhotoImageView = findViewById(R.id.imagen);
        mPhotoImageView.setOnClickListener(this);
        mContext = getApplicationContext();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {//si es de la galeria
            Uri imageUri = data.getData();
            String coso = data.getDataString();
            String co = data.getData().getPath();
            mPhotoImageView.setImageURI(imageUri);
            this.uriImagen = imageUri;


        }
        if (resultCode == RESULT_OK && requestCode == CAPTURA_FOTO) {
            if (imagen.exists()) {
                mPhotoImageView.setImageURI(fotoURI);
                this.uriImagen = fotoURI;

            }

        }
    }

    ////////////////FUNCIONES////////////////////////////

    /**
     * Funcion openGallery abre la galeria y permite seleccionar una imagen
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void openGallery() {//funcion que abre la galeria
        if (validaPermisosGaleria()) {//valido que se tengan los permisos
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

            startActivityForResult(gallery, PICK_IMAGE);//cargo el intento con el codigo 12
        }
    }

    /**
     * Funcion validarPermisosGaleria esta funcion verifica que se tengan los permisos suficientes, si no los tuviera los pide
     *
     * @return true
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean validaPermisosGaleria() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);//los pido aca si no los tengo
            return true;
        }
        return true;
    }

    @SuppressLint("WrongConstant")
    private void llamarCamara() {
        if (validaPermisosCamara()) {

            Intent inten = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String nombreFoto = getNombre();
            imagen = new File(pictureDirectory, nombreFoto + ".jpg");
            //  fotoURI = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), imagen);
            fotoURI = Uri.fromFile(new File(imagen.getPath()));
            inten.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
            if (inten.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(inten, CAPTURA_FOTO);

            }
        }

    }

    private String getNombre() {
        return "nombre";
    }

    private boolean validaPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2000);

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            } else {
                return true;
            }

        }
        return true;
    }

    private boolean validarPermisosEscritura() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2000);
        }
        return true;
    }

    private void denunciar() {//funcion denunciar
        String titulo = "prueba";
        String descripcion = "coso";
        int idEstado = 1;
        boolean activo = true;
        String latitud = "1.0952";
        String longitud = "0.256";

        try {
            // File fil = this.comprimirImagen(this.uriImagen);
            validarPermisosEscritura();
            File fil = FileUtil.from(this, this.uriImagen);
            File file = this.comprimirImagen2(fil);
            System.out.print("Sin Comprimir:" + fil.length());
            System.out.print("Comprimida: " + file.length());
            Api api = RetrofitCliente.getInstance().getApi();
            //File fil = new File("");
            RequestBody rqBody = RequestBody.create(MediaType.parse("multipart/form-data*"), file);
            MultipartBody.Part imagen = MultipartBody.Part.createFormData("foto", fil.getName(), rqBody);
            RequestBody tituloRB = RequestBody.create(MediaType.parse("multipart/form-data*"), titulo);
            RequestBody descripcionRB = RequestBody.create(MediaType.parse("multipart/form-data*"), descripcion);
            RequestBody latitudRB = RequestBody.create(MediaType.parse("multipart/form-data*"), latitud);
            RequestBody longitudRB = RequestBody.create(MediaType.parse("multipart/form-data*"), longitud);
            RequestBody idEstadoRB = RequestBody.create(MediaType.parse("multipart/form-data*"), String.valueOf(idEstado));
            RequestBody activoRB = RequestBody.create(MediaType.parse("multipart/form-data*"), String.valueOf(activo));
            Call<respuesta> call = api.crearDenuncia(imagen, tituloRB, descripcionRB, latitudRB, longitudRB, idEstadoRB, activoRB);
            call.enqueue(new Callback<respuesta>() {
                @Override
                public void onResponse(Call<respuesta> call, Response<respuesta> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCodigo() == "1") {
                            Toast.makeText(mContext, "Se ha dado de alta la denuncia", Toast.LENGTH_LONG);


                        }

                    }
                }

                @Override
                public void onFailure(Call<respuesta> call, Throwable t) {
                    Toast.makeText(mContext, "Fallo la conexion", Toast.LENGTH_LONG);
                }
            });


        } catch (Exception e) {
            System.err.print(e.getMessage());
        }

    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = (NetworkInfo) manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());


    }

    private File comprimirImagen(Uri selectedImage) {
        File fil = null;
        Bitmap originBitmap = null;
        InputStream imageStream;
        try {
            fil = (File) this.comprimirImagen2((File) FileUtil.from(this, selectedImage));


        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
        return fil;
    }

    private String getRealPathFromURI(Uri selectedImage) {
        String absolute = null;
        String canonical = null;
        try {
            File file = new File(selectedImage.getPath());
            absolute = file.getAbsolutePath();
            canonical = file.getCanonicalPath();
            // String path = file.getPath();
            System.out.print("absolute: " + absolute);
            System.out.print("canonical: " + canonical);
            // System.out.print("path" + path);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
        return absolute;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_foto:
                llamarCamara();
                break;
            case R.id.btn_denunciar:
                if (isConnected()) denunciar();
                break;

            case R.id.btnGaleria:
                openGallery();
                break;
        }
    }


    public File comprimirImagen2(File actualImage) {
        File imagenComprimida = null;

        if (actualImage == null) {
            // showError("Please choose an image!");
        } else {
            // Compress image in main thread using custom Compressor
            try {
                imagenComprimida = new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(actualImage);

                // setCompressedImage();
            } catch (IOException e) {
                e.printStackTrace();
                // showError(e.getMessage());
            }
        }
        return imagenComprimida;
    }
}
