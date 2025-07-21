package com.rohan.emvcardreaderlib

sealed interface CrState {
    object IDLE: CrState
    object Reset: CrState
    object PingConfig: CrState
    object RunConfig: CrState
    object PrePaidCardDataCollect: CrState
    object EmvSale: CrState
}