package com.xereon.xereon.data.util

import com.xereon.xereon.R
import com.xereon.xereon.data.model.Category

object CategoryUtils {

    /**
     * @see BitmapDescriptorFactory
     */
    private val CATEGORY_HUES = floatArrayOf(
        120f,   /* GREEN:  Lebensmittel*/
        0f,     /* RED:    Mode / Kleidung*/
        210f,   /* AZURE:  Dienstleistungen*/
        30f,    /* ORANGE: Restaurants*/
        330f,   /* ROSE:   Kosmetik*/
        0f,     /* RED:    Technik*/
        270f,   /* VIOLET: Unterhaltung*/
        60f,    /* YELLOW: Unterkunft*/
        330f    /* ROSE:   Medizinbedarf*/
    )

    fun getCategoryHue(category: Int)
            = if (category < CATEGORY_HUES.size) CATEGORY_HUES[category]
                else 0f

    private val CATEGORY_COLOR_RESOURCE_IDS = intArrayOf(
        R.color.type_green,  /*Lebensmittel*/
        R.color.type_red,  /*Mode / Kleidung*/
        R.color.type_azure,  /*Dienstleistungen*/
        R.color.type_orange,  /*Restaurants*/
        R.color.type_rose,  /*Kosmetik*/
        R.color.type_red,  /*Technik*/
        R.color.type_violet,  /*Unterhaltung*/
        R.color.type_yellow,  /*Unterkunft*/
        R.color.type_rose  /*Medizinbedarf*/
        /* RED:  Bauen & Wohnen, Medien & Spielwaren, Sportbedarf, Handwerk, Sonstiges*/
    )

    fun getCategoryColorResourceId(category: Int)
            = if (category < CATEGORY_COLOR_RESOURCE_IDS.size) CATEGORY_COLOR_RESOURCE_IDS[category]
                else R.color.type_red

    enum class Categories(val category: Int) {
        CATEGORY_LIVING(9),
        CATEGORY_SERVICES(2),
        CATEGORY_ELECTRONIC(5),
        CATEGORY_CRAFT(12),
        CATEGORY_HOTEL(7),
        CATEGORY_COSMETICS(4),
        CATEGORY_GROCERIES(0),
        CATEGORY_MEDIA_AND_TOYS(10),
        CATEGORY_MEDICINE(8),
        CATEGORY_CLOTH(1),
        CATEGORY_RESTAURANT(3),
        CATEGORY_SPORTS(11),
        CATEGORY_ENTERTAINMENT(6),
        CATEGORY_OTHERS(20),
    }

    private val mCategoryNames = arrayOf("Lebensmittel", "Mode / Kleidung", "Dienstleistung", "Restaurant",
        "Kosmetik", "Elektronik", "Unterhaltung", "Hotel", "Medizinbedarf", "Bauen & Wohnen", "Medien & Spielwaren",
        "Sportbedarf", "Handwerk", "Sonstiges")

    private val mCategories = arrayOf(
        Category(9, "Bauen & Wohnen", R.color.type_red, R.drawable.ic_living, -1,
            "Alles von Möbelgeschäften und Baumärkten bis zu Tierbedarf und Blumen als nette Geschenke gibt's hier alles!",
            arrayOf("Haushalt", "Möbel", "Bürobedarf", "Bettwaren", "Garten", "Tiere", "Blumen", "Baumarkt", "Baustoffe", "Recycling")
        ),
        Category(2, "Dienstleistungen", R.color.type_azure, R.drawable.ic_barber, -1,
            "Alles vom Friseur bis zur Nagelmaniküre!",
            arrayOf("Friseur", "Taxi", "Reisebüro", "Fahrschule", "Bank", "Post", "Haushaltshilfe", "Versicherung", "Waschanlage")
        ),
        Category(5, "Elektronik", R.color.type_red, R.drawable.ic_electronics, -1,
            "Alles von den neusten Handys bis zu Küchen-Ausstattung!",
            arrayOf("Technik", "Handy", "Auto", "Werkstatt")
        ),
        Category(12, "Handwerk", R.color.type_red, R.drawable.ic_craft, -1,
            "Wollt ihr fleißige Handwerker sehn? Alles von Heizungsinstalateuren bis zum Schreiner ist für jeden was dabei!",
            arrayOf("Fenster", "Heizung", "Isolationstechnik", "Klemptner", "Schlosser", "Schreiner")
        ),
        Category(7, "Hotel", R.color.type_yellow, R.drawable.ic_hotel, -1,
            "Alles von B&B's bis zu 5-Sterne-Hotels in Singapur!",
            arrayOf("Hotel", "Jugendherberge", "Campingplatz")
        ),
        Category(4, "Kosmetik", R.color.type_rose, -1, -1,
            "Einmal wieder hübsch machen gefällig? Mithilfe von Nagelstudios und Drogerien ist das ganz einfach!",
            arrayOf("Drogerie", "Nagelstudio", "Massage", "Parfümerie")
        ),
        Category(0, "Lebensmittel", R.color.type_green, R.drawable.ic_groceries, -1,
            "Alles von Supermärkten bis zum Bäcker und zur Käserei!",
            arrayOf("Supermarkt", "Bäcker", "Metzger", "Getränke", "Biomarkt", "Käserei", "Spirituosen", "Gewürze")
        ),
        Category(10, "Medien & Spielwaren", R.color.type_red, R.drawable.ic_toy, -1,
            "Ihre Kinder können Stundenlang nach den neusetn Spielzeugen suchen und Sie können sich einen schönen Film aussuchen!",
            arrayOf("Buchhandlung", "Filme", "Musik", "Zeitschriften", "Medien", "Musikinstrumente", "Spielwaren", "Unterhaltungsartikel")
        ),
        Category(8, "Medizinbedarf", R.color.type_rose, R.drawable.ic_medical, -1, "Aua! Was war das? Leiber schnell zu einem Arzt oder einer nahegelegenen Apotheke!",
            arrayOf("Apotheke", "Drogerie" , "Optiker", "Arzt")
        ),
        Category(1, "Mode / Kleidung", R.color.type_red, R.drawable.ic_cloth, -1,
            "Alles von Schmuck bis zu Schuhen und Alltagskleidung!",
            arrayOf("Bekleidung", "Mode", "Schuhe", "Accessoires", "Schmuck", "Sportbekleidung")
        ),
        Category(3, "Restaurant", R.color.type_orange, R.drawable.ic_restaurant, -1,
            "Alles von McDonalds bis zu noblen 5-Sterne-Restaurants!",
            arrayOf("Imbiss", "FastFood", "Pizzeria", "Traditionel", "Biergarten", "Chinesisch", "Griechisch", "Italienisch", "Mexikanisch", "Thailändisch")
        ),
        Category(11, "Sportbedarf", R.color.type_red, R.drawable.ic_sport, -1,
            "Fürs neue Jahr auch mehr Sport vorgenommen? Hier finden Sie alles, was sie führ Ihre Träume benötigen!",
            arrayOf("Sportartikel", "Fahrrad", "Fittnessstudio", "Sportanlage")
        ),
        Category(6, "Unterhaltung", R.color.type_violet, R.drawable.ic_entertainment, -1,
            "Alles von Kinos bis zu Kunstmuseen!",
            arrayOf("Kino", "Museum", "Zoo", "Theater", "Fittnessstudio", "Kiosk", "Casino", "Wettbüro", "Glücksspiel", "Nachtclub")
        ),
        Category(20, "Sonstiges", R.color.type_red, -1, -1,
            "Alles was sonst nirgendwo wirklich dazupasst",
            arrayOf("Tankstelle", "Geschenkeladen", "Tabakladen")
        )
    )

    fun getAllCategories() = mCategories
    fun getAllCategoryNames() = mCategoryNames

    fun getCategory(category: Categories) = mCategories[category.ordinal]
    fun getCategoryName(category: Categories) = mCategoryNames[category.ordinal]
    fun getCategoryName(categoryIndexInArray: Int) = mCategoryNames[categoryIndexInArray]

    fun getCategoryNameSize() = mCategoryNames.size
    fun getNumberOfCategories() = mCategories.size
}