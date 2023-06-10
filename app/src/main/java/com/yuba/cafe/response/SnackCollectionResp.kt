package com.yuba.cafe.response

data class SnackCollectionResp(
    val id: Long,
    val name: String,
    val snacks: List<com.yuba.cafe.model.Snack>,
    val type: com.yuba.cafe.model.CollectionType = com.yuba.cafe.model.CollectionType.Normal
)
