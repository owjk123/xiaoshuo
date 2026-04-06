package com.owjk.xiaoshuo.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.owjk.xiaoshuo.ui.screen.editor.EditorScreen
import com.owjk.xiaoshuo.ui.screen.home.HomeScreen
import com.owjk.xiaoshuo.ui.screen.settings.SettingsScreen
import com.owjk.xiaoshuo.ui.screen.worldbuild.WorldBuildScreen

object NavRoutes {
    const val HOME = "home"
    const val EDITOR = "editor/{bookId}"
    const val WORLD_BUILD = "worldbuild/{bookId}"
    const val SETTINGS = "settings"

    fun editor(bookId: Long) = "editor/$bookId"
    fun worldBuild(bookId: Long) = "worldbuild/$bookId"
}

@Composable
fun XiaoshuoNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.HOME) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onBookClick = { bookId -> navController.navigate(NavRoutes.editor(bookId)) },
                onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }
        composable(
            route = NavRoutes.EDITOR,
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) { backStack ->
            val bookId = backStack.arguments!!.getLong("bookId")
            EditorScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() },
                onWorldBuild = { navController.navigate(NavRoutes.worldBuild(bookId)) }
            )
        }
        composable(
            route = NavRoutes.WORLD_BUILD,
            arguments = listOf(navArgument("bookId") { type = NavType.LongType })
        ) { backStack ->
            val bookId = backStack.arguments!!.getLong("bookId")
            WorldBuildScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
