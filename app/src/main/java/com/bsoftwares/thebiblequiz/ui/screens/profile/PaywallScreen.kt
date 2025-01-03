package com.bsoftwares.thebiblequiz.ui.screens.profile

import android.util.Log
import androidx.compose.runtime.Composable
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener


@Composable
fun PaywallScreen(enablePremium: () -> Unit ,onDismissRequest: () -> Unit) {
    PaywallDialog(paywallDialogOptions = PaywallDialogOptions.Builder().setDismissRequest {
        onDismissRequest()
    }.setListener(
            object : PaywallListener {
                override fun onPurchaseCompleted(
                    customerInfo: CustomerInfo,
                    storeTransaction: StoreTransaction
                ) {
                    super.onPurchaseCompleted(customerInfo, storeTransaction)
                    Log.e("Paywall", "onPurchaseCompleted: ${customerInfo.originalAppUserId}")
                    enablePremium()
                    onDismissRequest()

                }

                override fun onPurchaseError(error: PurchasesError) {
                    super.onPurchaseError(error)
                    Log.e("Paywall", "onPurchaseError: ${error.message}")
                }

                override fun onPurchaseStarted(rcPackage: Package) {
                    super.onPurchaseStarted(rcPackage)
                    Log.e("Paywall", "onPurchaseStarted: ${rcPackage.offering}")
                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                    super.onRestoreCompleted(customerInfo)
                    Log.e("Paywall", "onRestoreCompleted: ${customerInfo.originalAppUserId}")

                }

                override fun onRestoreError(error: PurchasesError) {
                    super.onRestoreError(error)
                    Log.e("Paywall", "onRestoreError: ${error.message}")
                }

                override fun onRestoreStarted() {
                    super.onRestoreStarted()
                    Log.e("Paywall", "onRestoreStarted")
                }
            }).build()
    )
}