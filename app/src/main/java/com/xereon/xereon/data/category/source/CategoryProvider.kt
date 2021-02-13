package com.xereon.xereon.data.category.source

import android.content.Context
import com.xereon.xereon.R
import com.xereon.xereon.data.category.Categories
import com.xereon.xereon.data.category.Categories.*
import com.xereon.xereon.data.category.Category
import com.xereon.xereon.data.category.PopularCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val allCategories =  listOf(
        Category(
            CATEGORY_LIVING, context.getString(R.string.category_living), R.color.type_red,
            R.drawable.ic_living, -1, R.string.category_living_description,
            arrayOf("Haushalt", "Möbel", "Bürobedarf", "Bettwaren", "Garten", "Tiere", "Blumen", "Baumarkt", "Baustoffe", "Recycling")),

        Category(
            CATEGORY_SERVICES, context.getString(R.string.category_services), R.color.type_azure,
            R.drawable.ic_barber, -1, R.string.category_services_description,
            arrayOf("Friseur", "Taxi", "Reisebüro", "Fahrschule", "Bank", "Post", "Haushaltshilfe", "Versicherung", "Waschanlage")),

        Category(
            CATEGORY_ELECTRONIC, context.getString(R.string.category_electronics), R.color.type_red,
            R.drawable.ic_electronics, -1, R.string.category_electronics_description,
            arrayOf("Technik", "Handy", "Auto", "Werkstatt")),

        Category(CATEGORY_CRAFT, context.getString(R.string.category_craft), R.color.type_red,
            R.drawable.ic_craft, -1, R.string.category_craft_description,
            arrayOf("Fenster", "Heizung", "Isolationstechnik", "Klemptner", "Schlosser", "Schreiner")),

        Category(CATEGORY_HOTEL, context.getString(R.string.category_hotel), R.color.type_yellow,
            R.drawable.ic_hotel, -1, R.string.category_hotel_description,
            arrayOf("Hotel", "Jugendherberge", "Campingplatz")),

        Category(CATEGORY_COSMETICS, context.getString(R.string.category_cosmetics), R.color.type_rose, -1, -1,
            R.string.category_cosmetics_description,
            arrayOf("Drogerie", "Nagelstudio", "Massage", "Parfümerie")),

        Category(CATEGORY_GROCERIES, context.getString(R.string.category_groceries), R.color.type_green,
            R.drawable.ic_groceries, -1, R.string.category_groceries_description,
            arrayOf("Supermarkt", "Bäcker", "Metzger", "Getränke", "Biomarkt", "Käserei", "Spirituosen")),

        Category(CATEGORY_MEDIA_AND_TOYS, context.getString(R.string.category_media_toys), R.color.type_red,
            R.drawable.ic_toy, -1, R.string.category_media_toys_description,
            arrayOf("Buchhandlung", "Filme", "Musik", "Zeitschriften", "Medien", "Musikinstrumente", "Spielwaren")),

        Category(CATEGORY_MEDICINE, context.getString(R.string.category_medicine), R.color.type_rose,
            R.drawable.ic_medical, -1, R.string.category_medicine_description,
            arrayOf("Apotheke", "Drogerie", "Optiker", "Arzt")),

        Category(CATEGORY_CLOTH, context.getString(R.string.category_cloth), R.color.type_red,
            R.drawable.ic_cloth, -1, R.string.category_cloth_description,
            arrayOf("Bekleidung", "Mode", "Schuhe", "Accessoires", "Schmuck", "Sportbekleidung")),

        Category(CATEGORY_RESTAURANT, context.getString(R.string.category_restaurant), R.color.type_orange,
            R.drawable.ic_restaurant, -1, R.string.category_restaurant_description,
            arrayOf("Imbiss", "FastFood", "Pizzeria", "Traditionel", "Biergarten", "Chinesisch", "Griechisch", "Italienisch", "Mexikanisch", "Thailändisch")),

        Category(CATEGORY_SPORTS, context.getString(R.string.category_sports), R.color.type_red,
            R.drawable.ic_sport, -1, R.string.category_sports_description,
            arrayOf("Sportartikel", "Fahrrad", "Fittnessstudio", "Sportanlage")),

        Category(CATEGORY_ENTERTAINMENT, context.getString(R.string.category_entertainment), R.color.type_violet,
            R.drawable.ic_entertainment, -1, R.string.category_entertainment_description,
            arrayOf("Kino", "Museum", "Zoo", "Theater", "Fittnessstudio", "Kiosk", "Casino", "Wettbüro", "Glücksspiel", "Nachtclub")),

        Category(CATEGORY_OTHERS, context.getString(R.string.category_others), R.color.type_red,
            -1, -1, R.string.category_others_description,
            arrayOf("Tankstelle", "Geschenkeladen", "Tabakladen")),
    )

    fun getPopularCategories(onClick: (Categories) -> Unit = {}) = listOf(
        PopularCategory(
            CATEGORY_GROCERIES,
            R.string.category_groceries,
            R.drawable.ic_groceries,
            onClick
        ),
        PopularCategory(CATEGORY_CLOTH, R.string.category_cloth, R.drawable.ic_cloth, onClick),
        PopularCategory(
            CATEGORY_SERVICES,
            R.string.category_services,
            R.drawable.ic_barber,
            onClick
        ),
        PopularCategory(
            CATEGORY_RESTAURANT,
            R.string.category_restaurant,
            R.drawable.ic_restaurant,
            onClick
        ),
        PopularCategory(
            CATEGORY_ENTERTAINMENT,
            R.string.category_entertainment,
            R.drawable.ic_entertainment,
            onClick
        ),
        PopularCategory(CATEGORY_HOTEL, R.string.category_hotel, R.drawable.ic_hotel, onClick),
        PopularCategory(
            CATEGORY_ELECTRONIC,
            R.string.category_electronics,
            R.drawable.ic_electronics,
            onClick
        ),
    )

    fun getAllCategories() = allCategories
    fun getCategoryColor(id: Int): Int = allCategories[id].colorId
    fun getCategory(id: Int): Category = allCategories[id]
}