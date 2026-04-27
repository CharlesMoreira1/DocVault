package com.yape.common.helper

import android.content.Context
import android.hardware.biometrics.BiometricPrompt as PlatformBiometricPrompt
import android.os.CancellationSignal
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON
import androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED
import androidx.core.content.ContextCompat
import com.yape.common.R

fun showBiometricPrompt(
    context: Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val canAuthenticate = BiometricManager.from(context)
        .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        onError(context.getString(R.string.biometric_not_available))
        return
    }

    setupAuthenticate(context, onSuccess, onError)
}

private fun setupAuthenticate(
    context: Context,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(context)

    PlatformBiometricPrompt.Builder(context)
        .setTitle(context.getString(R.string.biometric_title))
        .setSubtitle(context.getString(R.string.biometric_subtitle))
        .setNegativeButton(context.getString(R.string.action_cancel), executor) { _, _ -> }
        .build()
        .authenticate(
            CancellationSignal(),
            executor,
            object : PlatformBiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: PlatformBiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != ERROR_NEGATIVE_BUTTON && errorCode != ERROR_USER_CANCELED) {
                        onError(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {}
            }
        )
}
