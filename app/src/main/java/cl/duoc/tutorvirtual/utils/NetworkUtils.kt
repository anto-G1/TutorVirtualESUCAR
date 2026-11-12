
package cl.duoc.tutorvirtual.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    fun hayConexion(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val red = connectivityManager.activeNetwork
            ?: return false

        val capacidades =
            connectivityManager.getNetworkCapabilities(red)
                ?: return false

        return capacidades.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) && capacidades.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )
    }
}
