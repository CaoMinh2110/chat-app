package com.truevibeup.core.network.util

import android.util.Base64
import android.util.Log
import org.json.JSONObject

object JwtTokenDebugger {
    private const val TAG = "JwtTokenDebugger"

    /**
     * Decode JWT token and log its claims
     * JWT format: header.payload.signature
     */
    fun debugToken(token: String) {
        try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e(TAG, "❌ Invalid JWT format: expected 3 parts, got ${parts.size}")
                return
            }

            Log.d(TAG, "✓ Token format valid (JWT with 3 parts)")

            // Decode header
            try {
                val headerJson = decodeBase64ToPrettyJson(parts[0])
                Log.d(TAG, "📋 Header: $headerJson")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode header", e)
            }

            // Decode payload (contains claims)
            try {
                val payloadJson = JSONObject(String(Base64.decode(parts[1], Base64.DEFAULT)))
                Log.d(TAG, "📦 Payload/Claims:")
                
                // Log all claims
                val iterator = payloadJson.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val value = payloadJson.get(key)
                    
                    when (key) {
                        "exp" -> {
                            // exp is in seconds since epoch (can be Int or Long)
                            val expirySeconds = when (value) {
                                is Long -> value
                                is Int -> value.toLong()
                                else -> (value as? Number)?.toLong() ?: 0L
                            }
                            val expiryTime = expirySeconds * 1000
                            val currentTime = System.currentTimeMillis()
                            val isExpired = currentTime > expiryTime
                            val status = if (isExpired) "❌ EXPIRED" else "✓ VALID"
                            Log.d(TAG, "  $key (exp): $expirySeconds - $status")
                        }
                        "iat" -> {
                            // iat is issued at time
                            Log.d(TAG, "  $key (issued at): $value")
                        }
                        else -> {
                            Log.d(TAG, "  $key: $value")
                        }
                    }
                }
                
                // Check expiry explicitly
                val expValue = payloadJson.opt("exp")
                if (expValue != null) {
                    val exp = when (expValue) {
                        is Long -> expValue
                        is Int -> expValue.toLong()
                        else -> (expValue as? Number)?.toLong() ?: 0L
                    }
                    
                    if (exp > 0) {
                        val expiryMs = exp * 1000
                        val currentMs = System.currentTimeMillis()
                        val timeLeft = (expiryMs - currentMs) / 1000
                        
                        if (timeLeft > 0) {
                            Log.d(TAG, "⏱️  Token expires in: $timeLeft seconds")
                        } else {
                            Log.e(TAG, "⏱️  ❌ Token EXPIRED! Expired ${-timeLeft} seconds ago")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode payload: ${e.message}", e)
            }

            // Signature is not decoded (it's the hash)
            Log.d(TAG, "🔐 Signature: ${parts[2].take(20)}... (not verified by client)")

        } catch (e: Exception) {
            Log.e(TAG, "Error debugging token", e)
        }
    }

    private fun decodeBase64ToPrettyJson(base64String: String): String {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedString = String(decodedBytes)
            JSONObject(decodedString).toString(2)
        } catch (e: Exception) {
            String(Base64.decode(base64String, Base64.DEFAULT))
        }
    }

    /**
     * Check if token is valid (not expired)
     */
    fun isTokenValid(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return false

            val payloadJson = JSONObject(String(Base64.decode(parts[1], Base64.DEFAULT)))
            val expValue = payloadJson.opt("exp") ?: return true // No expiry claim
            
            val exp = when (expValue) {
                is Long -> expValue
                is Int -> expValue.toLong()
                else -> (expValue as? Number)?.toLong() ?: return true
            }
            
            if (exp <= 0) return true // No expiry
            
            val expiryMs = exp * 1000
            val currentMs = System.currentTimeMillis()
            currentMs < expiryMs
        } catch (e: Exception) {
            Log.e(TAG, "Error checking token validity", e)
            false
        }
    }

    /**
     * Get token expiry time in seconds
     */
    fun getTokenExpirySeconds(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payloadJson = JSONObject(String(Base64.decode(parts[1], Base64.DEFAULT)))
            val expValue = payloadJson.opt("exp") ?: return null
            
            when (expValue) {
                is Long -> expValue
                is Int -> expValue.toLong()
                else -> (expValue as? Number)?.toLong()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting token expiry", e)
            null
        }
    }

    /**
     * Get time remaining until token expires (in seconds)
     */
    fun getTimeUntilExpiry(token: String): Long? {
        return try {
            val exp = getTokenExpirySeconds(token) ?: return null
            val currentTime = System.currentTimeMillis() / 1000
            val timeLeft = exp - currentTime
            if (timeLeft > 0) timeLeft else null
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating time until expiry", e)
            null
        }
    }
}
