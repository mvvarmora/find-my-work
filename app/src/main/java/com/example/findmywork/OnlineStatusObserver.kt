package com.example.findmywork

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.findmywork.data.repository.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class OnlineStatusObserver(
    private val firestoreRepository: FirestoreRepository,
    private val getUserId: () -> String?
) : LifecycleEventObserver {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                val uid = getUserId()
                if (uid != null) {
                    scope.launch {
                        firestoreRepository.updateWorkerField(uid, "isOnline", false)
                    }
                }
            }
            else -> {}
        }
    }
}
