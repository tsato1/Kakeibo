package com.kakeibo.core.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.core.domain.use_cases.KkbAppUseCases
import com.kakeibo.core.util.UiText
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

abstract class KkbAppViewModel: ViewModel() {

    @Inject
    protected lateinit var kkbAppUseCases: KkbAppUseCases

    private val _kkbAppModelState = mutableStateOf(KkbAppModelState())
    val kkbAppModelState: State<KkbAppModelState> = _kkbAppModelState

    init {
        loadKkbAppStates()
    }

    fun loadKkbAppStates() {
        if (this::kkbAppUseCases.isInitialized) {
            viewModelScope.launch {
                kkbAppUseCases.getKkbAppUseCase()?.let { kkbAppModel ->
                    _kkbAppModelState.value = kkbAppModelState.value.copy(
                        kkbAppModel = kkbAppModel
                    )
                }
            }
        }
    }

    suspend fun showAds(): Long {
        val result = kkbAppUseCases.insertKkbAppUseCase(
            KkbAppEntity(
                kkbAppModelState.value.kkbAppModel.id,
                kkbAppModelState.value.kkbAppModel.name,
                kkbAppModelState.value.kkbAppModel.type,
                Calendar.getInstance().toYMDString(UtilDate.DATE_FORMAT_DB_KMS),
                kkbAppModelState.value.kkbAppModel.intVal1,
                0,
                kkbAppModelState.value.kkbAppModel.intVal3,
                kkbAppModelState.value.kkbAppModel.strVal1,
                kkbAppModelState.value.kkbAppModel.strVal2,
                kkbAppModelState.value.kkbAppModel.strVal3
            )
        )
        loadKkbAppStates()
        return result
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: UiText) : UiEvent()
    }

}