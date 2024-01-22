package com.hussein.tictactocclient.data

import kotlinx.serialization.Serializable

@Serializable
data class MakeTurn(
    val x:Int,
    val y:Int
)
