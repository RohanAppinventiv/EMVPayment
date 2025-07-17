package com.rohan.emvcardreaderlib

sealed interface CrState {
    object IDLE: CrState
    object PrePaidCardDataCollect: CrState
    object EmvSale: CrState
}