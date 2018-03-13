package br.com.ayla.mapas

import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DialogTitle
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import android.content.DialogInterface
import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleAPiClient : GoogleApiClient
    val REQUEST_GPS = 212

    override fun onConnected(p0: Bundle?) {
       Log.i("TAG" , "Conectando")
        checkPermission()

        val minhaLocalizacao = LocationServices.FusedLocationApi.getLastLocation(mGoogleAPiClient)

        if (minhaLocalizacao != null){
            adicionaMarcador(minhaLocalizacao.latitude, minhaLocalizacao.longitude, "Minha localizacao")
        }

    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permissão para gravar negada")

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                val builder = AlertDialog.Builder(this)

                builder.setMessage("Necessária a permissao para GPS")
                        .setTitle("Permissao Requerida")

                builder.setPositiveButton("OK") { dialog, id ->
                    Log.i("TAG", "Clicked")
                    requestPermission()
                }

                val dialog = builder.create()
                dialog.show()

            } else {
                requestPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GPS -> {
                if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("TAG", "Permissão negada pelo usuário")
                } else {
                    Log.i("TAG", "Permissao concedida pelo usuario")
                }
                return
            }
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_GPS)
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i("TAG", "SUSPENSO")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i("TAG", "CANCELADO")
    }

    fun callConnection(){
        mGoogleAPiClient = GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build()

        mGoogleAPiClient.connect()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btPesquisar.setOnClickListener{

            mMap.clear()
            val geoCoder = Geocoder(this)
            var address : List<Address>?

            address = geoCoder.getFromLocationName(etEndereco.text.toString(), 1)
            if (address.isNotEmpty()){
                val location = address[0]

                adicionaMarcador(location.latitude, location.longitude, "Endereco pesquisado")
            }else{
               // Toast().show()
                var alert = AlertDialog.Builder(this).create()
                alert.setTitle("Ops! Deu ruim")
                alert.setMessage("Endereço não encontrado!")
                alert.setCancelable(false)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK"){
                    dialogInterface, inteiro -> alert.dismiss()
                }
                alert.show()
            }


        }
    }

    fun adicionaMarcador(latitude: Double, longitude: Double, title: String){
        val sydney = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions()
                .position(sydney)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher) )
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        callConnection()
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-23.5341085, -46.6524089)
//        mMap.addMarker(MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher) )
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))
    }
}
