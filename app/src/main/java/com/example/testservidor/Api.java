package com.example.testservidor;

import android.text.Editable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @Multipart
    @POST("nuevoevento")
    Call<respuesta> crearDenuncia(@Part MultipartBody.Part file,
                                  @Part("nombre") RequestBody titulo,
                                  @Part("descripcion") RequestBody descripcion,
                                  @Part("latitud") RequestBody latitud,
                                  @Part("longitud") RequestBody longitud,
                                  @Part("idEstado") RequestBody idStado,
                                  @Part("activo") RequestBody activo);


}

