package com.insearching.urbansports.core.domain.util

sealed interface DataError: Error {
    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIAlIZATION,
        UNKNOWN
    }
    
    enum class Local: DataError {
        DATA_ERROR,
        PERMISSION_REQUIRED,
        GPS_DISABLED,
        NO_LOCATION_FOUND
    }
}