package com.example.testservidor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class respuesta {

    @SerializedName("codigo")
    private String codigo;
    @SerializedName("mensaje")
    private String mensaje;
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
