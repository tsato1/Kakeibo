package com.kakeibo.feature_settings.presentation.custom_category_detail.components

import android.util.Xml
import android.widget.Toast
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.SwapVerticalCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.divyanshu.draw.widget.DrawView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.presentation.custom_category_detail.CustomCategoryDetailViewModel
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.presentation.components.BannerAds
import com.kakeibo.core.presentation.components.TransparentHintTextField
import com.kakeibo.feature_settings.presentation.custom_category_detail.CustomCategoryDetailEvent
import com.kakeibo.ui.theme.LightCream
import com.kakeibo.ui.theme.ThickCream
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import java.lang.Exception

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
fun CustomCategoryDetailScreen(
    navController: NavController,
    viewModel: CustomCategoryDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val initialCategoryType = remember { mutableStateOf(UtilCategory.CATEGORY_COLOR_EXPENSE) }

    val categoryIdState = viewModel.categoryId.value
    val categoryNameState = viewModel.categoryName.value
    val categoryTypeState = viewModel.categoryType
    val categoryImageState = viewModel.categoryImage
    val bitmapToSave = remember {
        mutableStateOf(
            UtilDrawing.createDefaultBitmap(
                context.resources.getDimension(R.dimen.draw_view_canvas_sides).toInt(),
                context.resources.getDimension(R.dimen.draw_view_canvas_sides).toInt()
            )
        )
    }

    val strokeWidth = remember { mutableStateOf(20f) }
    val openStrokeWidthDialog = remember { mutableStateOf(false) }
    val onClearClick = remember { mutableStateOf(false) }
    val onUndoClick = remember { mutableStateOf(false) }
    val onRedoClick = remember { mutableStateOf(false) }
    val openSaveDialog = remember { mutableStateOf(false) }

    val imageBorderAnimatable = remember { Animatable(initialValue = Color.Black) }

    val cardBackgroundColor = LightCream
    val strokeColor = ThickCream.toArgb()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CustomCategoryDetailViewModel.UiEvent.Init -> {
                    imageBorderAnimatable.animateTo(
                        targetValue = CategoryModel.types[categoryTypeState.value].second,
                        animationSpec = tween(
                            durationMillis = 500
                        )
                    )
                    initialCategoryType.value = categoryTypeState.value
                }
                is CustomCategoryDetailViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
                }
                is CustomCategoryDetailViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG).show()
                }
                is CustomCategoryDetailViewModel.UiEvent.Save -> {
                    Toast.makeText(context, R.string.msg_item_successfully_saved, Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = 3,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(cardBackgroundColor)
                        .border(
                            width = 2.dp,
                            color = imageBorderAnimatable.value,
                            shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(0.dp, 30.dp, 0.dp, 0.dp),
                        text = "Step 1"
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(0.dp, 60.dp, 0.dp, 0.dp),
                        text = stringResource(id = R.string.choose_category_color)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CategoryModel.types.forEach { type ->
                            val colorInt = type.first // UtilCategory.EXPENSE or INCOME
                            val colorColor = type.second
                            val stringId = type.third

                            Box(
                                modifier = Modifier
                                    .height(70.dp)
                                    .width(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 3.dp,
                                        color = if (categoryTypeState.value == colorInt) {
                                            Color.Black
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .drawBehind {
                                        drawRect(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(colorColor, Color.Transparent),
                                                startX = 0f,
                                                endX = size.width
                                            )
                                        )
                                    }
                                    .clickable {
                                        if (categoryIdState != -1L && initialCategoryType.value != colorInt) {
                                            Toast.makeText(
                                                context,
                                                R.string.msg_type_cannot_be_changed,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        else {
                                            scope.launch {
                                                imageBorderAnimatable.animateTo(
                                                    targetValue = colorColor,
                                                    animationSpec = tween(
                                                        durationMillis = 500
                                                    )
                                                )
                                            }
                                            scope.launch {
                                                pagerState.animateScrollToPage(1, 0f)
                                            }
                                            viewModel.onEvent(
                                                CustomCategoryDetailEvent.TypeChanged(
                                                    colorInt
                                                )
                                            )
                                        }
                                    }
                            ) {
                                Text(
                                    text = stringResource(id = stringId),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                1 -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(cardBackgroundColor)
                        .border(
                            width = 2.dp,
                            color = imageBorderAnimatable.value,
                            shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(0.dp, 30.dp, 0.dp, 0.dp),
                        text = "Step 2"
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(0.dp, 60.dp, 0.dp, 0.dp),
                        text = stringResource(id = R.string.enter_category_name)
                    )
                    TransparentHintTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp, 0.dp)
                            .align(Alignment.Center),
                        text = categoryNameState.text,
                        hint = categoryNameState.hint,
                        onValueChange = {
                            if (it.length <= 8)
                                viewModel.onEvent(CustomCategoryDetailEvent.NameEntered(it))
                        },
                        onFocusChange = {
                            viewModel.onEvent(CustomCategoryDetailEvent.NameFocusChanged(it))
                        },
                        isHintVisible = categoryNameState.isHintVisible,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.body1
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Button(
                            modifier = Modifier
                                .height(40.dp)
                                .padding(20.dp, 0.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0, 0f)
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.previous))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier
                                .height(40.dp)
                                .padding(20.dp, 0.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(2, 0f)
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.next))
                        }
                    }
                }
                2 -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(cardBackgroundColor)
                        .border(
                            width = 2.dp,
                            color = imageBorderAnimatable.value,
                            shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    Text(
                        text = "Step 3",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(0.dp, 30.dp, 0.dp, 0.dp),
                    )
                    Text(
                        text = stringResource(id = R.string.draw_icon),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(0.dp, 60.dp, 0.dp, 0.dp),
                    )
                    Column(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        AndroidView(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(dimensionResource(id = R.dimen.draw_view_canvas_sides))
                                .height(dimensionResource(id = R.dimen.draw_view_canvas_sides))
                                .clip(CircleShape),
                            factory = {
                                val parser: XmlPullParser =
                                    context.resources.getXml(R.xml.draw_view)
                                try {
                                    parser.next()
                                    parser.nextTag()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                val attrs = Xml.asAttributeSet(parser)
                                DrawView(it, attrs)
                            }
                        ) { drawView ->
                            if (categoryIdState != -1L) {
                                drawView.background = UtilDrawing.bitmapToDrawalbe(
                                    context,
                                    UtilDrawing.replaceColorExcept(
                                        categoryImageState.value,
                                        strokeColor,
                                        imageBorderAnimatable.value.toArgb()
                                    )
                                )
                            } else {
                                drawView.setBackgroundColor(imageBorderAnimatable.value.toArgb())
                            }

                            drawView.setColor(strokeColor)
                            drawView.setStrokeWidth(strokeWidth.value)

                            if (onClearClick.value) {
                                drawView.clearCanvas()
                                onClearClick.value = false
                            }
                            if (onUndoClick.value) {
                                drawView.undo()
                                onUndoClick.value = false
                            }
                            if (onRedoClick.value) {
                                drawView.redo()
                                onRedoClick.value = false
                            }
                            if (openSaveDialog.value) {
                                bitmapToSave.value = drawView.getBitmap()
                            }
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .background(MaterialTheme.colors.onBackground),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = { onClearClick.value = true }
                            ) {
                                Icon(imageVector = Icons.Default.Circle, contentDescription = "")
                            }
                            IconButton(
                                onClick = { onUndoClick.value = true }
                            ) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                            }
                            IconButton(
                                onClick = { onRedoClick.value = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = ""
                                )
                            }
                            IconButton(
                                onClick = { openStrokeWidthDialog.value = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVerticalCircle,
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Button(
                            modifier = Modifier
                                .height(40.dp)
                                .padding(20.dp, 0.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1, 0f)
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.previous))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(20.dp, 0.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            onClick = { openSaveDialog.value = true }
                        ) {
                            Text(text = stringResource(R.string.save))
                        }
                    }
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
        )
        if (viewModel.kkbAppState.value.intVal2 == ConstKkbAppDB.AD_SHOW) {
            BannerAds(
                adId = stringResource(id = R.string.main_banner_ad)
            )
        }
    }

    if (openStrokeWidthDialog.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            onDismissRequest = { openStrokeWidthDialog.value = false},
            text = { stringResource(id = R.string.stroke_thickness) },
            buttons = {
                val small = dimensionResource(id = R.dimen.draw_view_thickness_small)
                val medium = dimensionResource(id = R.dimen.draw_view_thickness_medium)
                val large = dimensionResource(id = R.dimen.draw_view_thickness_large)
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    onClick = {
                        strokeWidth.value = small.value
                        openStrokeWidthDialog.value = false
                    }
                ) {
                    Text(text = small.toString())
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    onClick = {
                        strokeWidth.value = medium.value
                        openStrokeWidthDialog.value = false
                    }
                ) {
                    Text(text = medium.toString())
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    onClick = {
                        strokeWidth.value = large.value
                        openStrokeWidthDialog.value = false
                    }
                ) {
                    Text(text = large.toString())
                }
            },
            shape = RoundedCornerShape(15.dp)
        )
    }
    
    if (openSaveDialog.value) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_mikan),
                    contentDescription = "Icon Not Found",
                    tint= Color.Unspecified
                )
            },
            onDismissRequest = {
                openSaveDialog.value = false
            },
            text = {
                Text(
                    text = stringResource(id = R.string.quest_category_creation_do_you_want_to_proceed),
                    color = Color.Black
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { openSaveDialog.value = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        openSaveDialog.value = false
                        viewModel.onEvent(CustomCategoryDetailEvent.Save(bitmapToSave.value))
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        )
    }

}