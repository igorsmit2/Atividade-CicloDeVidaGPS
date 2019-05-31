package br.usjt;

import android.content.Context;

import java.util.List;
import java.util.Locale;

public class LocationDAO {
    private Context context;
    public LocationDAO(Context context){
        this.context = context;
    }

    // INSERT
    public String insertLocation(List<Localizacao> loc){
        String template = "INSERT INTO %s (%s, %s) VALUES (%d, %d);";
        StringBuilder sb = new StringBuilder ("");
        for (Localizacao localizacao : loc){
            sb.append(
                    String.format(
                            Locale.getDefault(),
                            template,
                            LocationContract.Location.TABLE_NAME,
                            LocationContract.Location.COLUMN_NAME_LAT,
                            LocationContract.Location.COLUMN_NAME_LON,

                            localizacao.latitude,
                            localizacao.longitude
                    )
            );
        }
        return sb.toString();
    }
}
