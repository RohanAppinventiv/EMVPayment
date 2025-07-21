package com.quivioedge.emvpayment

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quivioedge.emvlib.pos.EMVBridge
import com.quivioedge.emvpayment.screens.CardReaderScreen
import com.quivioedge.emvpayment.screens.PaymentScreen
import com.quivioedge.emvpayment.ui.theme.EMVPaymentTheme
import com.rohan.emvcardreaderlib.PRINT_TAG
import com.rohan.emvcardreaderlib.manager.DsiEMVManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val posManager by lazy {
        EMVBridge(this)
    }
    private val cardReaderManager by lazy {
        DsiEMVManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            EMVPaymentTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "payment"
                    ) {
                        composable("payment") {
                            PaymentScreen(
                                modifier = Modifier.padding(innerPadding),
                                posManager,
                                showSnackBar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                },
                                navController = navController
                            )
                        }
                        composable("card_reader") {
                            CardReaderScreen(
                                modifier = Modifier.padding(innerPadding),
                                cardReaderManager,
                                showSnackBar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                },
                                onBack = {navController.navigateUp()},
                                isCardReaderConnected = false,
                                onConfigure = {
                                    Log.d(PRINT_TAG, "OnConfigure: ")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}