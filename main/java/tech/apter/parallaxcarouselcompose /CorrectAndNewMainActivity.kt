@file:Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")

package tech.adapter.parallaxcarouselcompose

import android.os.Bundle
import android.content.res.Resources
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tech.apter.parallaxcarouselcompose.ui.ParallaxCarousel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParallaxCarouselComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ParallaxCarousel(resources)
                    }
                }
            }
        }
    }

    private fun setContent(function: () -> ParallaxCarouselComposeTheme) {
        TODO("Not yet implemented")
    }
}

class ParallaxCarouselComposeTheme(function: @Composable () -> Unit) {

}

@Composable
fun ParallaxCarouselPreview() {
    ParallaxCarouselComposeTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val resources =  null
            var resource = resources
        }
    }
}

fun ParallaxCarousel(resource: Any) {
    TODO("Not yet implemented")
}
