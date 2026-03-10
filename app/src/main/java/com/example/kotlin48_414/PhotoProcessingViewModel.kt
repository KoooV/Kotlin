package com.example.kotlin48_414

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

sealed class LocationState {
    data object Idle : LocationState()
    data object Loading : LocationState()
    data class Success(
        val address: String,
        val lat: Double,
        val lng: Double
    ) : LocationState()
    data class Error(val message: String) : LocationState()
}

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(application)

    private val _state = MutableStateFlow<LocationState>(LocationState.Idle)
    val state: StateFlow<LocationState> = _state

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        _state.value = LocationState.Loading

        val context = getApplication<Application>()

        // Проверяем, включён ли GPS/сеть
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!gpsEnabled && !networkEnabled) {
            _state.value = LocationState.Error("GPS и сеть отключены. Включите геолокацию в настройках.")
            return
        }

        viewModelScope.launch {
            try {
                val location = getCurrentLocation()
                if (location == null) {
                    _state.value = LocationState.Error("Не удалось получить координаты. Попробуйте ещё раз.")
                    return@launch
                }

                val lat = location.latitude
                val lng = location.longitude
                val address = reverseGeocode(context, lat, lng)

                _state.value = LocationState.Success(
                    address = address,
                    lat = lat,
                    lng = lng
                )
            } catch (e: Exception) {
                _state.value = LocationState.Error("Ошибка: ${e.localizedMessage ?: "неизвестная ошибка"}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): android.location.Location? =
        suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            fusedClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }

            cont.invokeOnCancellation { cts.cancel() }
        }

    private suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): String {
        return suspendCancellableCoroutine { cont ->
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        cont.resume(formatAddress(addresses.firstOrNull()))
                    }
                    override fun onError(errorMessage: String?) {
                        cont.resume("Адрес не определён (ошибка геокодера)")
                    }
                })
            } else {
                @Suppress("DEPRECATION")
                val addresses = try {
                    geocoder.getFromLocation(lat, lng, 1)
                } catch (_: Exception) {
                    null
                }
                cont.resume(formatAddress(addresses?.firstOrNull()))
            }
        }
    }

    private fun formatAddress(address: Address?): String {
        if (address == null) return "Адрес не найден"
        val parts = mutableListOf<String>()
        address.thoroughfare?.let { parts.add(it) }
        address.subThoroughfare?.let { parts.add(it) }
        address.locality?.let { parts.add(it) }
        address.adminArea?.let { parts.add(it) }
        address.countryName?.let { parts.add(it) }
        return if (parts.isNotEmpty()) parts.joinToString(", ")
        else address.getAddressLine(0) ?: "Адрес не найден"
    }

    fun reset() {
        _state.value = LocationState.Idle
    }
}
