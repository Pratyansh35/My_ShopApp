package com.parawale.GrocEase.drawerPanel.leftPanel

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.parawale.GrocEase.R
import com.parawale.GrocEase.database.Slidess
import com.parawale.GrocEase.mainScreen.MenuSlide
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.sign_in.listofAuthorizedUsersEmails
import kotlinx.coroutines.CoroutineScope

val SettingsItems = listOf(
    Slidess(
        "Manage Account", "edit your account details", R.drawable.edit_account_logo
    ), Slidess(
        "Notifications", "Notification, Language", R.drawable.edit_account_logo
    ), Slidess(
        "Address", "Manage your address", R.drawable.edit_account_logo
    ), Slidess(
        "Payments", "Manage your payment details", R.drawable.edit_account_logo
    ), Slidess(
        "Connect Printer", "Select your bluetooth printing", R.drawable.ic_location
    ), Slidess(
        "App Layout", "Change your app layout", R.drawable.ic_location
    )
)

@Composable
fun Settings(
    navController: NavController? = null,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    userData: UserData?
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 10.dp)
            .scrollable(
                rememberScrollState(),
                orientation = androidx.compose.foundation.gestures.Orientation.Vertical
            )
    ) {
        items(SettingsItems) { Slidess ->
            val isAuthorized = Slidess.Type !in listOf(
                "Customers Order",
                "Add Items",
                "Connect Printer"
            ) || (Slidess.Type in listOf("Connect Printer") && listofAuthorizedUsersEmails.contains(
                userData?.userEmail
            ))
            if (isAuthorized) {
                MenuSlide(
                    Slidess,
                    scope = scope,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            }
        }
    }
}