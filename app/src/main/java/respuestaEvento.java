import com.example.testservidor.evento;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class respuestaEvento
{
    @SerializedName("codigo")
    private String codigo;
    @SerializedName("mensaje")
    private String mensaje;
    @SerializedName("eventos")
    private List<evento> eventos;

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

    public List<evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<evento> eventos) {
        this.eventos = eventos;
    }
}
