package com.darker.appsettings.app.ui.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darker.appsettings.ui.theme.AppSettings2Theme
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class PerAppSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val title = intent.getStringExtra("title") ?: packageName
        val packageName = intent.getStringExtra("packageName") ?: packageName
        val icon = packageManager.getApplicationIcon(packageName)
        setContent {
            AppSettings2Theme {
                Actionbar(title, icon)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Actionbar(title: String, icon: Drawable) {
    LargeTopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(7.dp)),
                    painter = rememberDrawablePainter(icon),
                    contentDescription = title
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}