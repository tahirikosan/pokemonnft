package com.tahirikosan.pokemonnft.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tahirikosan.pokemonnft.data.remote.Resource
import org.json.JSONObject

object Utils {
    fun showToastShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    // Enables view or not.
    fun View.enable(enabled: Boolean) {
        isEnabled = enabled
        alpha = if (enabled) 1f else 0.5f
    }

    // Makes view visible or gone
    fun View.visible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun EditText.trimmedText() = this.text.trim().toString()

    fun Fragment.onBackPressed(onBackPressed: () -> Unit) {
        this.requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed.invoke()
        }
    }

    fun pokemonImageUrlGenerateById(pokemonId: Int) =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemonId}.png"

    fun String.toJsonObject(): JSONObject {
        val obj = JSONObject()
        obj.put("message", this)
        return obj
    }

    fun View.snackbar(message: String, action: (() -> Unit)? = null) {
        val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        action?.let {
            snackbar.setAction("Retry") {
                it()
            }
        }
        snackbar.show()
    }

    fun Fragment.handleApiError(
        failure: Resource.Failure,
        retry: (() -> Unit)? = null
    ) {
        when {
            failure.isNetworkError -> requireView().snackbar(
                "Please check your internet connection",
                retry
            )
            failure.errorCode == 401 -> {
                activity?.finish()
                // (activity as Ac<*>).clearPreferences()
                //logout()
            }
            failure.isFirebaseError -> requireView().snackbar(
                failure.errorMessage!!,
                retry
            )
            else -> {
                val error = failure.errorBody?.string().toString()
                requireView().snackbar(error)
            }
        }
    }


    fun closeKeyboard(activity: Activity) {
        // this will give us the view
        // which is currently focus
        // in this layout
        val view: View? = activity.currentFocus

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            val manager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}