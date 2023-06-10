package com.yuba.cafe.ui.home.manage

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuba.cafe.R
import com.yuba.cafe.model.Snack
import com.yuba.cafe.ui.components.JetsnackButton
import com.yuba.cafe.ui.components.JetsnackDivider
import com.yuba.cafe.ui.components.JetsnackSurface
import com.yuba.cafe.ui.components.SnackImage
import com.yuba.cafe.ui.theme.JetsnackTheme
import com.yuba.cafe.ui.utils.formatPrice

@Composable
fun ManageMenu(
    application: Application,
    modifier: Modifier = Modifier,
    viewModel: ManageSnackViewModel = viewModel(
        factory = ManageSnackViewModel.provideFactory(
            application
        )
    )
) {

    val showingSnack by viewModel.showingSnack.collectAsStateWithLifecycle()
    val toggle by viewModel.toggle.collectAsStateWithLifecycle()

    BackHandler(enabled = toggle) {
        if (toggle) {
            viewModel.toggleView()
        }
    }

    fun onSnackClick(snack: Snack) {
        viewModel.onOrderClick(snack)
        viewModel.toggleView()
    }

    JetsnackSurface(modifier = modifier.fillMaxHeight()) {

        if (!toggle)
            AllSnackView(viewModel) { snack -> onSnackClick(snack) }
        else
            SnackAddOrEditView(viewModel, showingSnack)
    }
}

@Composable
private fun AllSnackView(
    viewModel: ManageSnackViewModel,
    onSnackClick: (Snack) -> Unit
) {
    val allSnacks by viewModel.snacks.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getAllSnacks()
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.manage_menu),
                    style = MaterialTheme.typography.h5,
                    color = JetsnackTheme.colors.brand,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                        .wrapContentHeight()
                )
            }
        }
        items(allSnacks) { snack ->
            SnackItem(snack, onSnackClick)
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                JetsnackButton(
                    onClick = {
                        onSnackClick(Snack(0, "", "", 0, "", emptySet(), 0))
                    },
                ) {
                    Text(
                        text = "Add New Menu",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
        }
    }
}

@Composable
private fun SnackItem(
    snack: Snack,
    onSnackClick: (Snack) -> Unit,
    modifier: Modifier = Modifier
) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSnackClick(snack) }
            .background(JetsnackTheme.colors.uiBackground)
            .padding(horizontal = 24.dp)

    ) {
        val (divider, image, name, tag, priceSpacer, price, remove, quantity) = createRefs()
        createVerticalChain(name, tag, priceSpacer, price, chainStyle = ChainStyle.Packed)
        SnackImage(
            imageUrl = snack.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                }
        )
        Text(
            text = snack.name,
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textSecondary,
            modifier = Modifier.constrainAs(name) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = remove.start,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Text(
            text = snack.tagline,
            style = MaterialTheme.typography.body1,
            color = JetsnackTheme.colors.textHelp,
            modifier = Modifier.constrainAs(tag) {
                linkTo(
                    start = image.end,
                    startMargin = 16.dp,
                    end = parent.end,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )
        Spacer(
            Modifier
                .height(8.dp)
                .constrainAs(priceSpacer) {
                    linkTo(top = tag.bottom, bottom = price.top)
                }
        )
        Text(
            text = formatPrice(snack.price),
            style = MaterialTheme.typography.subtitle1,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.constrainAs(price) {
                linkTo(
                    start = image.end,
                    end = quantity.start,
                    startMargin = 16.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
            }
        )

        Row(modifier = Modifier.constrainAs(quantity) {
            baseline.linkTo(price.baseline)
            end.linkTo(parent.end)
        }) {
            Text(
                text = stringResource(R.string.quantity),
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                modifier = Modifier
                    .padding(end = 18.dp)
                    .align(Alignment.CenterVertically)
            )

            Text(
                text = "${snack.available}",
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 24.dp)
            )
        }

        JetsnackDivider(
            Modifier.constrainAs(divider) {
                linkTo(start = parent.start, end = parent.end)
                top.linkTo(parent.bottom)
            }
        )
    }
}

@Composable
private fun SnackAddOrEditView(
    viewModel: ManageSnackViewModel,
    snack: Snack
) {

    val name = remember { mutableStateOf(snack.name) }
    val imageUrl = remember { mutableStateOf(snack.imageUrl) }
    val price = remember { mutableStateOf(snack.price) }
    val tagline = remember { mutableStateOf(snack.tagline) }
    val tags = remember { mutableStateOf(tagsToString(snack.tags)) }
    val available = remember { mutableStateOf(snack.available) }
    val detail = remember { mutableStateOf(snack.detail) }
    val ingredients = remember { mutableStateOf(snack.ingredients) }

    fun addOrUpdate() {
        viewModel.addOrUpdateSnack(
            snack.id,
            name.value,
            imageUrl.value,
            price.value,
            tagline.value,
            stringToTags(tags.value),
            available.value,
            detail.value,
            ingredients.value
        )
    }

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {

        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (snack.id == 0L) stringResource(R.string.add_new_menu) else stringResource(
                        R.string.menu_details
                    ),
                    style = MaterialTheme.typography.h5,
                    color = JetsnackTheme.colors.brand,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .wrapContentHeight()
                )
            }
        }

        item {
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {
            TextField(
                value = imageUrl.value,
                onValueChange = { imageUrl.value = it },
                label = { Text("Image Url") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {

            TextField(
                value = "${price.value}",
                onValueChange = {
                    var newVal = it
                    if (newVal.isEmpty()) {
                        newVal = "0"
                    }
                    price.value = newVal.toLong()
                },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {

            TextField(
                value = tagline.value,
                onValueChange = { tagline.value = it },
                label = { Text("Tag Line") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {

            TextField(
                value = tags.value,
                onValueChange = { tags.value = it },
                label = { Text("Tags (Comma Separated)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {

            TextField(
                value = "${available.value}",
                onValueChange = {
                    var newVal = it
                    if (newVal.isEmpty()) {
                        newVal = "0"
                    }
                    available.value = newVal.toLong()
                },
                label = { Text("Available") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {

            TextField(
                value = detail.value,
                onValueChange = {
                    detail.value = it
                },
                label = { Text("Detail") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }
        item {

            TextField(
                value = ingredients.value,
                onValueChange = {
                    ingredients.value = it
                },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = JetsnackTheme.colors.uiBackground,
                    focusedLabelColor = JetsnackTheme.colors.brand,
                    unfocusedLabelColor = JetsnackTheme.colors.brand,
                )
            )
        }

        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
        }

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                JetsnackButton(
                    onClick = {
                        addOrUpdate()
                    },
                ) {
                    Text(
                        text = if (snack.id == 0L) "Save" else "Update",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
        }
    }
}

fun tagsToString(tags: Set<String>): String {
    return tags.joinToString(",")
}

fun stringToTags(tagStr: String): Set<String> {

    val tags = tagStr.split(",")
    val tagSet = mutableSetOf<String>()
    for (tag in tags) {
        if (tag.isNotEmpty()) {
            tagSet.add(tag.trim())
        }
    }
    return tagSet
}