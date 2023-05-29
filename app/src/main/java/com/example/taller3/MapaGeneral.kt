package com.example.taller3

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MapaGeneral : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marcadores: ArrayList<Marker>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapa_general)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_general) as SupportMapFragment
        mapFragment.getMapAsync(this)

        marcadores = ArrayList()

        val btnPedirServicio = findViewById<Button>(R.id.btn_pedirServicio)
        val btnAceptarServicio = findViewById<Button>(R.id.btn_aceptarServicio)

        btnPedirServicio.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val ubicacionUsuarioActual = LatLng(location.latitude, location.longitude)
                        val marcadorMasCercano = encontrarMarcadorMasCercano(ubicacionUsuarioActual)
                        marcadorMasCercano?.let {
                            // Realiza la acción deseada con el marcador más cercano, por ejemplo, muestra un mensaje
                            val distancia = calcularDistancia(ubicacionUsuarioActual, it.position)
                            Toast.makeText(
                                this,
                                "El marcador más cercano está a una distancia de $distancia km",
                                Toast.LENGTH_LONG
                            ).show()

                            // Agregar una línea recta entre el usuario y el marcador más cercano
                            agregarLineaRectaEntreMarcadores(ubicacionUsuarioActual,it.position)

                            val nombreVeterinario = it.title

                            // Crear el intent y pasar el nombre del veterinario a la actividad DetallesServicio
                            val intent = Intent(this, DetallesServicio::class.java)
                            intent.putExtra("nombreVeterinario", nombreVeterinario)

                        } ?: run {
                            Toast.makeText(
                                this,
                                "No se encontraron marcadores",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        btnAceptarServicio.setOnClickListener {
            val intent = Intent(this, DetallesServicio::class.java)
            startActivity(intent)
        }
    }

    private fun encontrarMarcadorMasCercano(ubicacionActual: LatLng): Marker? {
        var marcadorMasCercano: Marker? = null
        var distanciaMasCercana: Float = Float.MAX_VALUE

        for (marker in marcadores) {
            val ubicacionMarcador = marker.position
            val distancia = calcularDistancia(ubicacionActual, ubicacionMarcador)

            if (distancia < distanciaMasCercana) {
                distanciaMasCercana = distancia
                marcadorMasCercano = marker
            }
        }

        return marcadorMasCercano
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mapa = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurar opciones de mapa
        mapa.uiSettings.isZoomGesturesEnabled = true
        mapa.uiSettings.isZoomControlsEnabled = true
        mapa.uiSettings.isMyLocationButtonEnabled = true


        // Obtener la ubicación actual del usuario y mostrarla en el mapa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val ubicacionUsuarioActual = LatLng(location.latitude, location.longitude)
                    mapa.addMarker(
                        MarkerOptions().position(ubicacionUsuarioActual).title("Ubicación actual")
                    )

                    // Llamar a la función para agregar los marcadores de los veterinarios
                    agregarMarcadoresVeterinarios(ubicacionUsuarioActual)
                    agregarLineaRectaEntreMarcadores(ubicacionUsuarioActual, ubicacionUsuarioActual)
                }
            }
        }
    }

    private fun agregarMarcadoresVeterinarios(ubicacionUsuarioActual: LatLng) {
        val mRootReference = FirebaseDatabase.getInstance().reference.child("veterinarios")

        val builder = LatLngBounds.Builder()

        mRootReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val latitudString = snapshot.child("latitud").getValue(String::class.java)
                    val longitudString = snapshot.child("longitud").getValue(String::class.java)
                    val nombreCompleto = snapshot.child("nombre").getValue(String::class.java)

                    val latitud = latitudString?.replace(",", ".")?.toDoubleOrNull()
                    val longitud = longitudString?.replace(",", ".")?.toDoubleOrNull()

                    if (latitud != null && longitud != null && nombreCompleto != null) {
                        val ubicacionVeterinario = LatLng(latitud, longitud)
                        val marker = mapa.addMarker(MarkerOptions().position(ubicacionVeterinario).title(nombreCompleto))
                        if (marker != null) {
                            marcadores.add(marker)
                        } // Agrega esta línea para agregar el marcador a la lista

                        // Agregar la ubicación del veterinario y la ubicación actual a la cámara
                        builder.include(ubicacionVeterinario)
                    }
                }

                // Agregar la ubicación actual a la cámara
                builder.include(ubicacionUsuarioActual)

                // Construir los límites para la cámara
                val bounds = builder.build()

                // Calcular el padding para los límites de la cámara
                val padding = resources.getDimensionPixelSize(R.dimen.padding)

                // Mover la cámara a los límites que incluyen todos los marcadores y la ubicación actual
                mapa.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MapaGeneral, "Error al obtener los veterinarios: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun agregarLineaRectaEntreMarcadores(ubicacionActual: LatLng, ubicacionRastrear: LatLng) {
        // Agregar una línea recta entre los dos marcadores
        mapa.addPolyline(
            PolylineOptions()
                .add(ubicacionActual, ubicacionRastrear)
                .color(Color.RED)
        )

        // Calcular la distancia entre las dos ubicaciones
        val distancia = calcularDistancia(ubicacionActual, ubicacionRastrear)

        // Mostrar la distancia en el mapa
        val distanciaText = String.format("%.2f km", distancia)
        val distanciaMarker = MarkerOptions()
            .position(obtenerPuntoMedio(ubicacionActual, ubicacionRastrear))
            .title(distanciaText)
        mapa.addMarker(distanciaMarker)
    }

    private fun calcularDistancia(ubicacion1: LatLng, ubicacion2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            ubicacion1.latitude, ubicacion1.longitude,
            ubicacion2.latitude, ubicacion2.longitude,
            results
        )
        return results[0] / 1000  // Convertir a kilómetros
    }

    private fun obtenerPuntoMedio(ubicacion1: LatLng, ubicacion2: LatLng): LatLng {
        val latitudMedia = (ubicacion1.latitude + ubicacion2.latitude) / 2
        val longitudMedia = (ubicacion1.longitude + ubicacion2.longitude) / 2
        return LatLng(latitudMedia, longitudMedia)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Llamar a onMapReady() nuevamente para mostrar ambos marcadores después de obtener los permisos
                onMapReady(mapa)
            } else {
                Toast.makeText(this,"Permiso de ubicación denegado",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_disconnect -> {

                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginUsuario::class.java)
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_LONG).show()
                startActivity(intent)
                finish()
                return true
            }
            R.id.menu_available -> {

                val firebaseDatabase = FirebaseDatabase.getInstance()
                val firebaseAuth = FirebaseAuth.getInstance()

                val updateData = HashMap<String, Any>()
                updateData["estado"] = "Disponible"
                firebaseDatabase.reference.child("usuarios").child(firebaseAuth.currentUser!!.uid).updateChildren(updateData)
                Toast.makeText(this, "Estado actualizado a disponible", Toast.LENGTH_LONG).show()

                return true
            }
            R.id.menu_unavailable -> {

                val firebaseDatabase = FirebaseDatabase.getInstance()
                val firebaseAuth = FirebaseAuth.getInstance()

                val updateData = HashMap<String, Any>()
                updateData["estado"] = "No disponible"
                firebaseDatabase.reference.child("usuarios").child(firebaseAuth.currentUser!!.uid).updateChildren(updateData)
                Toast.makeText(this, "Estado actualizado a no disponible", Toast.LENGTH_LONG).show()

                return true
            }

            R.id.btn_lista_usuarios -> {
                // Lógica para ir a la lista de usuarios
                val intent = Intent(this, ListaUsuarios::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}