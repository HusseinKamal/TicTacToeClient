package com.hussein.tictactocclient.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.tictactocclient.data.GameState
import com.hussein.tictactocclient.data.KtorRealtimeMessagingClient
import com.hussein.tictactocclient.data.MakeTurn
import com.hussein.tictactocclient.data.RealTimeMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class TicTacToeViewModel @Inject constructor(
    private val client: RealTimeMessaging
): ViewModel() {

    val state = client
        .getGameStateStream()
        .onStart {
            _isConnecting.value=true
        }
        .onEach {
            _isConnecting.value=false
        }
        .catch {t->
            _showConnectionError.value = t is ConnectException
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameState())

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting =_isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()

    fun finishTurn(x:Int , y:Int){
        if(state.value.field[y][x] != null || state.value.winningPlayer != null){
            return
        }
        viewModelScope.launch {
            client.sendAction(MakeTurn(x,y))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            client.close()
        }
    }
}