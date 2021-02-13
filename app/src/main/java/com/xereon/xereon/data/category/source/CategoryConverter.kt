package com.xereon.xereon.data.category.source

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

    fun getCategoryColor(id: Int): Int =
        if (id < CATEGORY_COLORS.size)
            CATEGORY_COLORS[id]
        else
            R.color.type_red

}