package ke.don.common_datasource.remote.domain.error_handler

import android.content.Context
import android.widget.Toast
import ke.don.shared_domain.states.NetworkResult

// Custom toaster utility
object Koffee {
    fun toast(context: Context, error: NetworkResult.Error) {
        /**
         * Gratitude is a must (yeah)
         *
         * This is a nod to Koffee's Toast. It shows a toast with the message and hint
         */

        Toast.makeText(context, "${error.message}. ${error.hint}", Toast.LENGTH_LONG).show()
    }
}