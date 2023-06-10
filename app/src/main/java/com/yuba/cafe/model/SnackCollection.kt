package com.yuba.cafe.model

import androidx.compose.runtime.Immutable
import com.yuba.cafe.response.SnackCollectionResp


enum class CollectionType { Normal, Highlight }

/**
 * A fake repo
 */
object SnackRepo {
    fun getSnacks(): List<SnackCollectionResp> = com.yuba.cafe.model.snackCollections
    fun getSnack(snackId: Long) = com.yuba.cafe.model.snacks.find { it.id == snackId }!!
    fun getRelated(@Suppress("UNUSED_PARAMETER") snackId: Long) = com.yuba.cafe.model.related
    fun getInspiredByCart() = com.yuba.cafe.model.inspiredByCart
    fun getFilters() = com.yuba.cafe.model.filters
    fun getPriceFilters() = com.yuba.cafe.model.priceFilters
    fun getCart() = com.yuba.cafe.model.cart
    fun getSortFilters() = com.yuba.cafe.model.sortFilters
    fun getCategoryFilters() = com.yuba.cafe.model.categoryFilters
    fun getSortDefault() = com.yuba.cafe.model.sortDefault
    fun getLifeStyleFilters() = com.yuba.cafe.model.lifeStyleFilters

    fun getProfile() = com.yuba.cafe.model.UserProfile(1, "Mahvish", "mahvish@gmail.com", "F", "USER")
}

/**
 * Static data
 */

private val tastyTreats = SnackCollectionResp(
    id = 1L,
    name = "Android's picks",
    type = com.yuba.cafe.model.CollectionType.Highlight,
    snacks = com.yuba.cafe.model.snacks.subList(0, 13)
)

private val popular = SnackCollectionResp(
    id = 2L,
    name = "Popular on YubaCafe",
    snacks = com.yuba.cafe.model.snacks.subList(14, 19)
)

private val wfhFavs = com.yuba.cafe.model.tastyTreats.copy(
    id = 3L,
    name = "WFH favourites"
)

private val newlyAdded = com.yuba.cafe.model.popular.copy(
    id = 4L,
    name = "Newly Added"
)

private val exclusive = com.yuba.cafe.model.tastyTreats.copy(
    id = 5L,
    name = "Only on YubaCafe"
)

private val also = com.yuba.cafe.model.tastyTreats.copy(
    id = 6L,
    name = "Customers also bought"
)

private val inspiredByCart = com.yuba.cafe.model.tastyTreats.copy(
    id = 7L,
    name = "Inspired by your cart"
)

private val snackCollections = listOf(
    com.yuba.cafe.model.tastyTreats,
    com.yuba.cafe.model.popular,
    com.yuba.cafe.model.wfhFavs,
    com.yuba.cafe.model.newlyAdded,
    com.yuba.cafe.model.exclusive
)

private val related = listOf(
    com.yuba.cafe.model.also,
    com.yuba.cafe.model.popular
)

private val cart = listOf(
    com.yuba.cafe.model.OrderLine(com.yuba.cafe.model.snacks[4], 2),
    com.yuba.cafe.model.OrderLine(com.yuba.cafe.model.snacks[6], 3),
    com.yuba.cafe.model.OrderLine(com.yuba.cafe.model.snacks[8], 1)
)

@Immutable
data class OrderLine(
    val snack: com.yuba.cafe.model.Snack,
    val count: Int
)
