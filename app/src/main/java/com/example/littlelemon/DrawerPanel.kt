package com.example.littlelemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//@Preview(showBackground = true)

@Composable
fun DrawerPanel(scaffoldState: ScaffoldState,scope: CoroutineScope, ){
Column(
)
{


    Row(modifier = Modifier.padding(top = 15.dp)){
        Image(painter = painterResource(id = R.drawable.mypic4),
            contentDescription = "UserImage",
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
        )


        Column(modifier = Modifier
            .padding(10.dp)
            .height(100.dp),
            verticalArrangement = Arrangement.Center) {
            Text(text = "Pratyansh Maddheshia",
                fontSize = 20.sp)
            Text(text = "+91-7007254934",
                fontSize = 15.sp)
        }
    }

    LazyColumn {
        items(SlidesItems) { Slidess ->
            MenuSlide(Slidess)
        }
    }
    IconButton(onClick = {
       scope.launch { scaffoldState.drawerState.close() }
    }) {
        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Close Icon")
    }
}
}



