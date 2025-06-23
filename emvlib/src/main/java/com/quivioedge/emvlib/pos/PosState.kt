package com.quivioedge.emvlib.pos

sealed interface PosState {
    object Idle: PosState
    object RequirePosConfig: PosState
    object ResetPad: PosState
    object EmvSaleCompleted: PosState
}