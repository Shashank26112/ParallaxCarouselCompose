@file:Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")

package tech.apter.parallaxcarouselcompose.ui

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import tech.apter.parallaxcarouselcompose.R
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color as ComposeColor
import kotlin.math.abs //Importing abs function from Kotlin standard library
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.palette.graphics.Palette


// Extension functions removed as they are not used
// private val <TextStyle> Any.h6: TextStyle
//    get() {}
// private val <TextStyle> Typography.h6: TextStyle
//    get() {}

// Reduced padding values
private val cardPadding = 0.9.dp
private val imagePadding = 0.8.dp

// Shadow and shape values for the card
private val shadowElevation = 0.0.dp
private val shape = RectangleShape

// Offset for the parallax effect
private val xOffset = cardPadding.value

private fun ImageBitmap.calculateDrawSize(density: Float, screenWidth: Dp, pagerHeight: Dp): IntSize {
    val originalImageWidth = width / density
    val originalImageHeight = height / density
    val imageAspectRatio = originalImageWidth / originalImageHeight
    val frameAspectRatio = screenWidth / pagerHeight
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
    val scaleFactor = 1f - 0.2f * abs(parallaxOffset / screenWidth.value)

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
                    dstSize = drawSize,
                )
            }
        }
    }
}

// Function to extract the color from an image using Palette
fun extractColorFromImage(imageResId: Int, resources: Resources): Int {
    val bitmap = BitmapFactory.decodeResource(resources, imageResId)
    val palette = Palette.from(bitmap).generate()
    return palette.getDominantColor(android.graphics.Color.BLACK)
}

// Function to get a contrasting color (white or black) based on the brightness of the dominant color
fun getContrastingColor(color: Int): Int {
    val darkness = 1 - (0.299 * android.graphics.Color.red(color) + 0.587 * android.graphics.Color.green(color) + 0.114 * android.graphics.Color.blue(color)) / 255
    return if (darkness < 0.5) android.graphics.Color.BLACK else android.graphics.Color.WHITE
}

class MainActivity : ComponentActivity() {
    @SuppressLint("AutoboxingStateCreation")
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val resources = resources

            // Remember the current page index and the dominant color
            var currentPage by remember { mutableStateOf(0) }
            var dominantColor by remember { mutableStateOf(Color.Black) }
            var textColor by remember { mutableStateOf(Color.White) }

            // Create a pager state
            val pagerState = rememberPagerState {
                listOf(
                    R.drawable.p1,
                    R.drawable.p2,
                    R.drawable.p3,
                    R.drawable.p4,
                    R.drawable.p5,
                    R.drawable.p6,
                    R.drawable.p7,
                ).size
            }

            // Update the dominant color and text color whenever the page changes
            LaunchedEffect(pagerState.currentPage) {
                currentPage = pagerState.currentPage
                val imageResId = when (currentPage) {
                    0 -> R.drawable.p1
                    1 -> R.drawable.p2
                    2 -> R.drawable.p3
                    3 -> R.drawable.p4
                    4 -> R.drawable.p5
                    5 -> R.drawable.p6
                    6 -> R.drawable.p7
                    else -> R.drawable.p1
                }
                val color = extractColorFromImage(imageResId, resources)
                dominantColor = Color(color)
                textColor = Color(getContrastingColor(color))
            }

            // Use the extracted color and contrasting text color as needed
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dominantColor)
            ) {
                // Toolbar with dynamic background and text color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(dominantColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Parallax Carousel", color = textColor, style = MaterialTheme.typography)
                } // Added closing brace here

                // Parallax carousel implementation
                ParallaxCarousel()
            }
        }
    }

    private fun Text(s: String, color: Color, style: Typography) {

    }
}
