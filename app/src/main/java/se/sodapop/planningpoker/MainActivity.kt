@file:OptIn(ExperimentalPagerApi::class)

package se.sodapop.planningpoker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.lerp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import se.sodapop.planningpoker.ui.theme.PlanningPokerTheme
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    private val cards =
        arrayListOf("0", "1", "2", "3", "5", "8", "13", "20", "40", "100", "?", "coffee")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlanningPokerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Column(Modifier.fillMaxSize()) {
                        val pagerState = rememberPagerState()

                        HorizontalPager(
                            count = cards.size,
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { page ->
                            Surface(
                                Modifier
                                    .padding(16.dp, 24.dp, 16.dp, 0.dp)
                                    .graphicsLayer {
                                        // Calculate the absolute offset for the current page from the
                                        // scroll position. We use the absolute value which allows us to mirror
                                        // any effects for both directions
                                        val pageOffset =
                                            calculateCurrentOffsetForPage(page).absoluteValue

                                        // We animate the scaleX + scaleY, between 85% and 100%
                                        lerp(
                                            start = 0.85f,
                                            stop = 1f,
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        ).also { scale ->
                                            scaleX = scale
                                            scaleY = scale
                                        }

                                        // We animate the alpha, between 50% and 100%
                                        alpha = lerp(
                                            start = 0.5f,
                                            stop = 1f,
                                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                        )
                                    }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colors.surface)
                                ) {

                                    TextOrImage(
                                        fontSize = 200.sp,
                                        imagePadding = 40.dp,
                                        text = cards[page]
                                    )
                                }
                            }
                        }

                        PagerIndicator(
                            pagerState = pagerState,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            cards = cards
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    pageCount: Int = pagerState.pageCount,
    indicatorWidth: Dp = 48.dp,
    indicatorHeight: Dp = indicatorWidth,
    cards: List<String>,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        FlowRow(
            mainAxisAlignment = MainAxisAlignment.Center,
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            val activeModifier = Modifier
                .size(width = indicatorWidth, height = indicatorHeight)
                .padding(8.dp)

            val activeBackgroundModifier = Modifier.background(color = MaterialTheme.colors.primary)
            val inactiveBackgroundModifier =
                Modifier.background(color = MaterialTheme.colors.surface)

            repeat(pageCount) {
                Box(modifier = Modifier.padding(8.dp)) {
                    val active = it == pagerState.currentPage
                    val backgroundModifier =
                        if (active) activeBackgroundModifier else inactiveBackgroundModifier
                    Box(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(it)
                                    }
                                }
                            )
                            .then(backgroundModifier.then(activeModifier)),
                        contentAlignment = Alignment.Center
                    )
                    {
                        TextOrImage(cards[it])
                    }
                }
            }
        }
    }
}

@Composable
fun TextOrImage(text: String, fontSize: TextUnit = TextUnit.Unspecified, imagePadding: Dp = 8.dp) {
    if (text == "coffee") {
        Box(modifier = Modifier.padding(imagePadding)) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.mug_hot_solid),
                contentDescription = "Coffee mug"
            )
        }
    } else {
        Text(text = text, fontSize = fontSize)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlanningPokerTheme {
        Greeting("Android")
    }
}