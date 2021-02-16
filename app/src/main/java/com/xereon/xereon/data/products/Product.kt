package com.xereon.xereon.data.products

data class Product(
    val id : Int,
    val name : String,
    val description: String = "----",
    val price : String = "0.00",
    val unit : Int,
    val appoffer : Boolean,
    val preorder: Boolean,
    val storeID: Int,
    val storeName: String,
    val otherStoreProducts: List<SimpleProduct>,
    val similar: List<SimpleProduct>
) {
    val productImageURL get() = "http://vordertuer.bplaced.net/app-img/products/$id.png"
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$storeID.png"
    val officeImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/store/$storeID"

    override fun toString() = "Product $name($id) from $storeName($storeID) costs $price $unit"
}