package br.usjt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Localizacao> localizacoes;
    private MyAdapter           adapter;
    private Context context;
    // API
    private LocationListener    locationListener;
    private LocationManager     locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // -------------------------------------------------------!
        recyclerView    = findViewById(R.id.recyclerView);
        localizacoes    = new ArrayList<>();
        adapter         = new MyAdapter(localizacoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Localizacao l = new Localizacao(lat, lon);

                LocationDAO dao = new LocationDAO(context);
                String sql = dao.insertLocation(localizacoes);
                GPSDBHelper db = new GPSDBHelper(context);
                db.insertLocation(sql);
                if(localizacoes.size() >= 50){
                    localizacoes.remove(0);
                    localizacoes.add(l);
                }else {
                    localizacoes.add(l);
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Chamou", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    // Recyler View
    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView latitudeTextView;
        TextView longitudeTextView;
        public MyViewHolder (View raiz){
            super(raiz);
            latitudeTextView = raiz.findViewById(R.id.latitudeTextView);
            longitudeTextView = raiz.findViewById(R.id.longitudeTextView);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        List<Localizacao> localizacoes;

        public MyAdapter(List<Localizacao> localizacoes){
            this.localizacoes = localizacoes;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View raiz = inflater.inflate(R.layout.list_item, viewGroup, false);
            return new MyViewHolder(raiz);
        }

        // ViewHolder
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            Localizacao localizacao = localizacoes.get(i);
            myViewHolder.latitudeTextView.setText(
                    Double.toString(localizacao.latitude)
            );
            myViewHolder.longitudeTextView.setText(
                    Double.toString(localizacao.longitude)
            );
        }

        @Override
        public int getItemCount() {
            return localizacoes.size();
        }
    }


    // Ciclo de Vida

    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1000
            );
        }
        else{
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1,
                    1,
                    locationListener
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1000){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1,
                            1,
                            locationListener
                    );
                }
            }
        }
    }
}
