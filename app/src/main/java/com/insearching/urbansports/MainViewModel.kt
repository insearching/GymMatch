package com.insearching.urbansports

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    private var _isPermissionsGranted = MutableStateFlow(false)
    val isPermissionsGranted = _isPermissionsGranted.asStateFlow()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeAt(0)
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
            _isPermissionsGranted.value = false
        }

        if (visiblePermissionDialogQueue.isEmpty()) {
            _isPermissionsGranted.value = true
        }
    }
}