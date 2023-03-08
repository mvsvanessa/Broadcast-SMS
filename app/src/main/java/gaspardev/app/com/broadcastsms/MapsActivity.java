package gaspardev.app.com.broadcastsms;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Conecta ao serviço de localização do Android
    private FusedLocationProviderClient servicoLocalizacao;
    private LocationRequest configuracaoGPS;
    private LocationCallback atualizacoesPosicao;

    private LatLng localizacao;
    private boolean permitiuGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Recuperação do gerenciador de localização
        LocationManager gpsHabilitado = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Verificação se o GPS está habilitado, caso não esteja…
        if (!gpsHabilitado.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //… abre a tela de configurações para habilitar o GPS ou não
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(getApplicationContext(), "Para este aplicativo é necessário habilitar o GPS", Toast.LENGTH_LONG).show();
        }

        //Chama o serviço de localização do Andrdoid e atribui ao objeto
        servicoLocalizacao = LocationServices.getFusedLocationProviderClient(this);

        configuracaoGPS = LocationRequest.create();
        configuracaoGPS.setInterval(20000);
        configuracaoGPS.setFastestInterval(5000);
        configuracaoGPS.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        atualizacoesPosicao = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    localizacao = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.e("TESTE_GPS", "Latitude: " + location.getLatitude());
                    Log.e("TESTE_GPS", "Longitude: " + location.getLongitude());

                    mMap.clear();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(localizacao.latitude,
                                    localizacao.longitude), 15));

                    // Add a marker in Sydney and move the camera
                    mMap.addMarker(new MarkerOptions().position(localizacao).title("Sua localização")).showInfoWindow();
                }
            }
        };
    }

    private void iniciaAtualizacoesGPS() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        servicoLocalizacao.requestLocationUpdates(configuracaoGPS,
                atualizacoesPosicao,
                Looper.getMainLooper());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        iniciaAtualizacoesGPS();
    }
}