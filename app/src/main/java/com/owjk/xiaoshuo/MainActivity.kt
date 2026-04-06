package com.owjk.xiaoshuo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.owjk.xiaoshuo.ui.XiaoshuoNavGraph
import com.owjk.xiaoshuo.ui.theme.XiaoshuoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XiaoshuoTheme {
                XiaoshuoNavGraph()
            }
        }
    }
}
