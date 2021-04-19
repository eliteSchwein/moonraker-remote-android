package de.eliteschw31n.moonrakerremote.utils

import com.google.android.material.textfield.TextInputLayout
import de.eliteschw31n.moonrakerremote.MainActivity
import java.util.*

class InputUtil {
    companion object {
        fun setError(textInputLayout: TextInputLayout, errorReason: String) {
            MainActivity.runUiUpdate(Runnable {
                textInputLayout.error = errorReason
                textInputLayout.isErrorEnabled = true
            })
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    MainActivity.runUiUpdate(Runnable {
                        textInputLayout.error = null
                        textInputLayout.isErrorEnabled = false
                    })
                }
            }, 2000)
        }

        fun toggleInputEdit(textInputLayout: TextInputLayout){
            MainActivity.runUiUpdate(Runnable {
                if(textInputLayout.editText?.isEnabled == true){
                    textInputLayout.editText?.isEnabled = false
                    return@Runnable
                }
                textInputLayout.editText?.isEnabled = true
            })
        }

        fun setSuccess(textInputLayout: TextInputLayout, message: String) {
            MainActivity.runUiUpdate(Runnable {
                textInputLayout.helperText = message
                textInputLayout.isHelperTextEnabled = true
            })
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    MainActivity.runUiUpdate(Runnable {
                        textInputLayout.helperText = null
                        textInputLayout.isHelperTextEnabled = false
                    })
                }
            }, 2000)
        }
    }
}