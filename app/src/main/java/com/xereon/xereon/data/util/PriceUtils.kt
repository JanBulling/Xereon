package com.xereon.xereon.data.util

object PriceUtils {

    private const val UNIT_PIECE_TINY = 0       /*per piece;  1 - 2*/
    private const val UNIT_PIECE_SMALL = 1      /*per piece;  1 - 5*/
    private const val UNIT_PIECE_MEDIUM = 2     /*per piece;  1 - 10*/
    private const val UNIT_PIECE_BIG = 3        /*per piece;  1 - 20*/
    private const val UNIT_PIECE_LARGE = 4      /*per 10 pieces;  1 - 100  in steps of 10*/
    private const val UNIT_WEIGHT_TINY = 5      /*per 10g;   in  10g steps to 50g*/
    private const val UNIT_WEIGHT_SMALL = 6     /*per 50g;   in  50g steps to 250g*/
    private const val UNIT_WEIGHT_MEDIUM = 7    /*per 100g;  in  50g steps to 500g*/
    private const val UNIT_WEIGHT_BIG = 8       /*per 100g;  in 100g steps to 1000g*/
    private const val UNIT_WEIGHT_LARGE = 9     /*per   kg;  in 500g steps to  5kg*/

    private val UNIT_STEPS =
        arrayOf(
            arrayOf("-", "1", "2"), //PIECE_TINY
            arrayOf("-", "1", "2", "3", "4", "5"),  //PIECE_SMALL
            arrayOf("-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),    //PIECE_MEDIUM
            arrayOf("-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"),
            arrayOf("-", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"),  //PIECE_LARGE
            arrayOf("-", "10", "20", "30", "40", "50"), //WEIGHT_TINY
            arrayOf("-", "50", "100", "150", "200", "250"), //WEIGHT_SMALL
            arrayOf("-", "50", "100", "150", "200", "250", "300", "350", "400", "450", "500"),  //WEIGHT_MEDIUM
            arrayOf("-", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000"),    //WEIGHT_BIG
            arrayOf("-", "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5")    //WEIGHT_LARGE
        )


    /**
     * @param price the price of the product as String in the following format: "10.00"
     * @param unit_index int representation of the Xereon-Unit-System
     * @return price + " € / " + the unit
     */
    fun getPriceWithUnitAsString(price: String, unit_index: Int): String? {
        val unitString = getCompleteUnitAsString(unit_index)
        return "$price € / $unitString"
    }

    /**
     * @param unit_index int representation of the Xereon-Unit-System
     * @return the String equivalent of the complete unit like "Stück", "10 Stück", "10g", "50g", ...
     */
    fun getCompleteUnitAsString(unit_index: Int)
            = when(unit_index) {
                UNIT_PIECE_TINY, UNIT_PIECE_SMALL, UNIT_PIECE_MEDIUM, UNIT_PIECE_BIG -> "Stück"
                UNIT_PIECE_LARGE -> "10 Stück"
                UNIT_WEIGHT_TINY -> "10g"
                UNIT_WEIGHT_SMALL -> "50g"
                UNIT_WEIGHT_MEDIUM, UNIT_WEIGHT_BIG -> "100g"
                UNIT_WEIGHT_LARGE -> "kg"
                else -> ""
            }

    /**
     * @param unit_index int representation of the Xereon-Unit-System
     * @return the String equivalent of the base unit like "Stück", "g", "kg", ...
     */
    fun getBaseUnit(unit_index: Int)
            = when(unit_index) {
                UNIT_PIECE_TINY, UNIT_PIECE_SMALL, UNIT_PIECE_MEDIUM, UNIT_PIECE_BIG, UNIT_PIECE_LARGE -> "Stück"
                UNIT_WEIGHT_TINY, UNIT_WEIGHT_SMALL, UNIT_WEIGHT_MEDIUM, UNIT_WEIGHT_BIG -> "10g"
                UNIT_WEIGHT_LARGE -> "kg"
                else -> ""
            }

    fun getStepsAsStringArray(unit_index: Int) = UNIT_STEPS[unit_index]
}