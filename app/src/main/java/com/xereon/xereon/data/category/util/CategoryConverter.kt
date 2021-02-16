package com.xereon.xereon.data.category.util

import com.xereon.xereon.R

object CategoryConverter {

    private val CATEGORY_COLORS = intArrayOf(
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

    fun getCategoryColor(id: Int): Int =
        if (id < CATEGORY_COLORS.size)
            CATEGORY_COLORS[id]
        else
            R.color.type_red

}