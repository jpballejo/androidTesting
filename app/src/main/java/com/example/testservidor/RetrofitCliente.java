package com.example.testservidor;
import android.net.ConnectivityManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCliente {
    private static String BASE_URL = "http://192.168.1.128:8888/denuncia/";
    private  Retrofit retrofit;
    private static RetrofitCliente mInstance;

    private RetrofitCliente(){
  try {
      this.retrofit = new Retrofit.Builder()
              .baseUrl(BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
  }catch (Exception e){
      System.out.print(e.getMessage());}


    }

    public static synchronized  RetrofitCliente getInstance(){
        if(mInstance==null){
            mInstance= new RetrofitCliente();
        }return mInstance;
       }
    public Api getApi(){return retrofit.create(Api.class);}



}
