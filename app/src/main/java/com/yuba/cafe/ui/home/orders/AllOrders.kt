package com.yuba.cafe.ui.home.orders

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuba.cafe.R
import com.yuba.cafe.model.order.Order
import com.yuba.cafe.model.order.OrderItem
import com.yuba.cafe.model.order.OrderState
import com.yuba.cafe.ui.components.JetsnackButton
import com.yuba.cafe.ui.components.JetsnackDivider
import com.yuba.cafe.ui.components.JetsnackSurface
import com.yuba.cafe.ui.components.SnackImage
import com.yuba.cafe.ui.theme.JetsnackTheme
import com.yuba.cafe.ui.utils.formatPrice

@Composable
fun AllOrders(
    application: Application,
    modifier: Modifier = Modifier,
    viewModel: AllOrderViewModel = viewModel(factory = AllOrderViewModel.provideFactory(application))
) {

    val showingOrder by viewModel.showingOrder.collectAsStateWithLifecycle()
    val toggle by viewModel.toggle.collectAsStateWithLifecycle()

    BackHandler(enabled = toggle) {
        if (toggle) {
            viewModel.toggleView()
        }
    }

    fun onOrderClick(order: Order) {
        viewModel.onOrderClick(order)
        viewModel.toggleView()
    }

    JetsnackSurface(modifier = modifier.fillMaxHeight()) {
        if (!toggle)
            AllOrderView(viewModel) { order -> onOrderClick(order) }
        else
            showingOrder?.let { SingleOrderDetail(viewModel, it) }
    }
}


@Composable
private fun AllOrderView(
    viewModel: AllOrderViewModel,
    onOrderClick: (Order) -> Unit
) {

    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getAllOrders()
    }

    LazyColumn(
        Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 25.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.all_orders),
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
        items(allOrders) { order ->
            OrderCard(
                order,
                onOrderClick
            )
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
fun OrderCard(
    order: Order,
    onOrderClick: (Order) -> Unit
) {

    val orderId = order.id
    val totalAmount = order.totalAmount
    val address = order.address
    val addressStr = "${address.name}\n${address.addressDetail}\nPincode: ${address.pinCode}"
    val orderState = order.orderState


    JetsnackSurface(
        shape = MaterialTheme.shapes.medium,
        elevation = 5.dp,
        modifier = Modifier.padding(
            start = 4.dp,
            end = 4.dp,
            bottom = 8.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = {
                    onOrderClick(order)
                })
                .padding(8.dp)
        ) {

            Text(
                text = "Order #$orderId",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = "Delivered At: ",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = addressStr,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = "Total: $totalAmount",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = "Order State: $orderState",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SingleOrderDetail(
    viewModel: AllOrderViewModel,
    order: Order,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
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
                    text = stringResource(R.string.order_details),
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

            Text(
                text = stringResource(R.string.order_id_header, order.id),
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight()
            )
        }
        items(order.orderItems) { orderLine ->
            OrderItem(orderLine)
        }
        item {
            SummaryItem(
                subtotal = order.totalAmount.toLong(),
                shippingCosts = 369,
            )

            Text(
                text = "Order Status: ${order.orderState}",
                style = MaterialTheme.typography.h6,
                color = JetsnackTheme.colors.brand,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 24.dp, vertical = 4.dp)
                    .wrapContentHeight()
            )

            if (viewModel.isManager() && order.orderState == OrderState.PENDING) {
                JetsnackButton(
                    onClick = {
                        viewModel.acceptOrder(order.id)
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.accept_order),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
                JetsnackButton(
                    onClick = {
                        viewModel.rejectOrder(order.id)
                    },
                    shape = RectangleShape,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.reject_order),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            Spacer(
                Modifier.windowInsetsTopHeight(
                    WindowInsets.statusBars.add(WindowInsets(top = 56.dp))
                )
            )
        }
    }
}

@Composable
fun OrderItem(
    orderLine: OrderItem,
    modifier: Modifier = Modifier
) {
    val snack = orderLine.snack
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
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
                text = "${orderLine.count}",
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
fun SummaryItem(
    subtotal: Long,
    shippingCosts: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.cart_summary_header),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.brand,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .heightIn(min = 56.dp)
                .wrapContentHeight()
        )
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(R.string.cart_subtotal_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(subtotal),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        JetsnackDivider()
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.cart_total_label),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .wrapContentWidth(Alignment.End)
                    .alignBy(LastBaseline)
            )
            Text(
                text = formatPrice(subtotal),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.alignBy(LastBaseline)
            )
        }
        JetsnackDivider()
    }
}
