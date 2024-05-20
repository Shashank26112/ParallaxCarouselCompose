 ParallaxCarouselCompose
Sample project
Our journey begins with the examination of the core components, and for those interested in exploring the detailed code snippets and the complete source code, you can find them all here: ParallaxCarouselCompose.

Let’s commence this transformation by delving into the key components.

TabView → HorizontalPager
To bridge the gap between SwiftUI’s TabView and Jetpack Compose, we turn our attention to HorizontalPager. SwiftUI’s TabView offers a delightful way to swipe through a collection of views seamlessly. In the realm of Android, our transition starts with HorizontalPager.

While it’s important to note that HorizontalPager is still considered experimental, it proves to be a fitting choice for replicating the core functionality of TabView in SwiftUI. With HorizontalPager, we’re able to navigate through a series of images with grace and fluidity, maintaining the desired user experience.
The parallaxOffset calculation is used to determine the position offset of the current page in the carousel. Here's why:

pagerState.getOffsetFractionForPage(page): This function retrieves a fractional value representing how far the current page is from its snapped position. It varies between -0.5 (if the page is offset towards the start of the layout) to 0.5 (if the page is offset towards the end of the layout). When the current page is in the snapped position, this value is 0.0. It's a useful indicator of the page's position within the carousel.
screenWidth: This is the width of the screen or the display area. Multiplying the fractional offset by the screen width helps scale the offset value to match the screen's dimensions. This step ensures that the parallax effect moves the image proportionally across the screen, making the effect visually pleasing and responsive to the screen size.
GeometryReader → Canvas
Upon examining the SwiftUI code, I noticed the use of GeometryReader, a critical component in achieving the parallax effect. In the realm of Jetpack Compose, we enlist the assistance of Canvas to bring this effect to life.

However, there’s a subtle difference. Unlike SwiftUI, where you can place an image inside GeometryReader with .aspectRatio(contentMode: .fill) to achieve the correct image ratio, Jetpack Compose takes a slightly different approach. Within Canvas, we can't directly use Compose Image. Instead, we employ the drawBitmap function of Canvas to render the images.

To replicate the behavior observed on iOS — an image that spans the full width (equivalent to the screen size) while maintaining the correct height — we delve into some calculation. This involves ensuring that our drawn image maintains the correct aspect ratio.

For those curious, here’s a glimpse of the calculateDrawSize function that handles this calculation.
