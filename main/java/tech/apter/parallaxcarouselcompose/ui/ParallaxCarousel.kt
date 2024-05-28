@file:Suppress("UNUSED_VARIABLE", "UseExpressionBody")

package tech.apter.parallaxcarouselcompose.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import tech.apter.parallaxcarouselcompose.R
import kotlin.math.roundToInt

// Reduced padding values
private val cardPadding = 0.9.dp
private val imagePadding = 0.8.dp

// Shadow and shape values for the card
private val shadowElevation = 0.0.dp
private val borderRadius = 0.dp
private val shape = RectangleShape

// Offset for the parallax effect
private val xOffset = cardPadding.value

private fun ImageBitmap.calculateDrawSize(density: Float, screenWidth: Dp, pagerHeight: Dp): IntSize {
    val originalImageWidth = width / density
    val originalImageHeight = height / density

    val frameAspectRatio = screenWidth / pagerHeight
    val imageAspectRatio = originalImageWidth / originalImageHeight

    val drawWidth = if (frameAspectRatio > imageAspectRatio) {
        screenWidth.value
    } else {
        pagerHeight.value * imageAspectRatio
    }

    val drawHeight = if (frameAspectRatio > imageAspectRatio) {
        screenWidth.value / imageAspectRatio
    } else {
        pagerHeight.value
    }

    return IntSize(
        width = drawWidth.toIntPx(density),
        height = drawHeight.toIntPx(density)
    )
}

// Extension function to convert Float to Int in pixels
private fun Float.toIntPx(density: Float) = (this * density).roundToInt()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParallaxCarousel() {
    // Get screen dimensions and density
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val density = LocalDensity.current.density

    // List of image resources
    val images = listOf(
        R.drawable.p1,
        R.drawable.p2,
        R.drawable.p3,
        R.drawable.p4,
        R.drawable.p5,
        R.drawable.p6,
        R.drawable.p7,
    )

    // Create a pager state
    val pagerState = rememberPagerState {
        images.size
    }

    // Calculate the height for the pager
    val pagerHeight = screenHeight / 1.5f

    // HorizontalPager composable: Swiping through images
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(pagerHeight)
            .padding(0.dp), // Ensure there's no padding on the pager itself
        contentPadding = PaddingValues(horizontal = 16.dp), // Adjust this value for left and right padding
        pageSpacing = 8.dp // Adjust this value for spacing between pages
    ) { page ->
        ParallaxCarouselItem(
            imageResId = images[page],
            parallaxOffset = 0f, // Calculate this value based on scroll position for parallax effect
            pagerHeight = pagerHeight,
            screenWidth = screenWidth,
            density = density
        )
    }
}

@Composable
fun ParallaxCarouselItem(
    imageResId: Int,
    parallaxOffset: Float,
    pagerHeight: Dp,
    screenWidth: Dp,
    density: Float,
) {
    // Load the image bitmap
    val imageBitmap = ImageBitmap.imageResource(id = imageResId)

    // Calculate the draw size for the image
    val drawSize = imageBitmap.calculateDrawSize(density, screenWidth, pagerHeight)

    // Calculate the scale factor based on the parallax offset
    val scaleFactor = 1f - 0.2f * kotlin.math.abs(parallaxOffset / screenWidth.value)

    // Card composable for the item
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(cardPadding)
            .background(Color.Green, shape)
            .shadow(shadowElevation, shape = shape)
    ) {
        // Canvas for drawing the image with parallax effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(imagePadding)
                .clip(RectangleShape)
        ) {
            // Calculate the scale to ensure the image fits within the view boundaries
            val scaleX = size.width / drawSize.width.toFloat()
            val scaleY = size.height / drawSize.height.toFloat()
            val scale = maxOf(scaleX, scaleY)

            // Translate the canvas for parallax effect
            translate(left = parallaxOffset * density) {
                // Draw the image
                drawImage(
                    image = imageBitmap,
                    srcSize = IntSize(imageBitmap.width, imageBitmap.height),
                    dstOffset = IntOffset(-xOffset.toIntPx(density), 0),
                    dstSize = drawSize,
                )
            }
        }
    }
}

@SuppressLint("ModifierFactoryExtensionFunction")
@Suppress("UNUSED_PARAMETER")
private fun Modifier.Companion.fillMaxSize(fraction: Dp) {
    // This function is unnecessary and should be removed if not used
}

// Function to extract the color from an image using Palette
fun extractColorFromImage(imageResId: Int, resources: android.content.res.Resources): Int {
    val bitmap = BitmapFactory.decodeResource(resources, imageResId)
    val palette = Palette.from(bitmap).generate()
    return palette.getDominantColor(Color.Black.toArgb())
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val resources = resources
            val color = extractColorFromImage(R.drawable.p1, resources)
            val backgroundColor = Color(color)

            // Use the extracted color as needed, for example:
            Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
                ParallaxCarousel()
            }
        }
    }
}
