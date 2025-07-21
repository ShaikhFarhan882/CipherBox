package apps.farhan.cipherbox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.util.Base64

class EncryptionViewModel : ViewModel() {

    private val _encryptedText = MutableLiveData<String>()
    val encryptedText: LiveData<String> = _encryptedText

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun encryptText(input: String, method: String) {
        if (input.isEmpty()) {
            _errorMessage.value = "Please enter text to encrypt"
            _encryptedText.value = ""
            return
        }

        viewModelScope.launch {
            _errorMessage.value = ""

            try {
                val result = withContext(Dispatchers.IO) {
                    when (method) {
                        "Base64" -> encodeBase64(input)
                        "MD5" -> hashMD5(input)
                        "SHA-1" -> hashSHA1(input)
                        "SHA-256" -> hashSHA256(input)
                        "SHA-512" -> hashSHA512(input)
                        else -> throw IllegalArgumentException("Unknown encryption method: $method")
                    }
                }
                _encryptedText.value = result.toString()
            } catch (e: Exception) {
                _errorMessage.value = "Encryption failed: ${e.message}"
            }
        }
    }

    private fun encodeBase64(input: String): String {
        return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.DEFAULT).trim()
    }

    private fun hashMD5(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("MD5 algorithm not available", e)
        }
    }

    private fun hashSHA1(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-1")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-1 algorithm not available", e)
        }
    }

    private fun hashSHA256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-256 algorithm not available", e)
        }
    }

    private fun hashSHA512(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-512")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-512 algorithm not available", e)
        }
    }


}