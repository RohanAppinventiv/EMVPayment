package com.quivioedge.emvpayment.pos

sealed interface PosState {
    object Idle: PosState
    object RequirePosConfig: PosState
    object ResetPad: PosState
    object EmvSaleCompleted: PosState
}