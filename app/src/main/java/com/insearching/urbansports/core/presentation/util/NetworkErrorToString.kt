package com.insearching.urbansports.core.presentation.util

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.R

fun DataError.toUiText(): UiText {
    val resId = when (this) {
        DataError.Local.DATA_ERROR -> R.string.data_error
        DataError.Local.PERMISSION_REQUIRED -> R.string.error_permission_required
        DataError.Local.GPS_DISABLED -> R.string.error_gps_disabled
        DataError.Local.NO_LOCATION_FOUND -> R.string.error_no_location_found
        DataError.Remote.REQUEST_TIMEOUT -> R.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> R.string.error_no_internet
        DataError.Remote.SERVER -> R.string.error_unknown
        DataError.Remote.SERIAlIZATION -> R.string.error_serialization
        DataError.Remote.UNKNOWN -> R.string.error_unknown

    }
    return UiText.StringResource(resId)
}