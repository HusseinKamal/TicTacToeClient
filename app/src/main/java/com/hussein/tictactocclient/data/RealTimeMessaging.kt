package com.hussein.tictactocclient.data

import kotlinx.coroutines.flow.Flow


interface RealTimeMessaging {
    fun getGameStateStream() : Flow<GameState>
    suspend fun sendAction(action:MakeTurn)
    suspend fun close()
}