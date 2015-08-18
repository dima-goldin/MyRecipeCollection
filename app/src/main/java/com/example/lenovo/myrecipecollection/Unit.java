package com.example.lenovo.myrecipecollection;

/**
 * Created by Lenovo on 2/3/2015.
 */
public enum Unit{
    EMPTY(""),
    GRAM("גרם"),
    ML("מיליליטר"),
    LITER("ליטר"),
    TEASPOON("כפיות"),
    TABLESPOON("כפות"),
    CUP("כוסות"),
    BOX("מיכלי"),
    PACKAGE("חבילות"),
    KG("קילוגרם");
    //TODO add more

    private String fieldDescription;

    private Unit(String fieldDescription)
    {

        this.fieldDescription=fieldDescription;
    }
    public void setFieldDescription(String fieldDescription)
    {

        this.fieldDescription=fieldDescription;

    }
    @Override
    public String toString()
    {
        return fieldDescription;
    }
    static public Unit returnUnitByInt(int field)
    {
        switch (field)
        {
            case 0:return Unit.EMPTY;
            case 1:return Unit.GRAM;
            case 2:return Unit.ML;
            case 3:return Unit.LITER;
            case 4:return Unit.TEASPOON;
            case 5:return Unit.TABLESPOON;
            case 6:return Unit.CUP;
            case 7:return Unit.BOX;
            case 8:return Unit.PACKAGE;
            case 9: return Unit.KG;
            default:return Unit.EMPTY;

        }
    }
    public int getInt()
    {
        switch (fieldDescription)
        {
            case "":return 0;
            case "גרם":return 1;
            case "מיליליטר":return 2;
            case "ליטר":return 3;
            case "כפיות":return 4;
            case "כפות":return 5;
            case "כוסות":return 6;
            case "מיכלי":return 7;
            case "חבילות":return 8;
            case "קילוגרם": return 9;


        }
        return 0;
    }

}
