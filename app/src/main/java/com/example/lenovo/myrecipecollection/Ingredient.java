package com.example.lenovo.myrecipecollection;


public class Ingredient {
    private Double amount;
   private Unit unit;
    private String name;

    public Ingredient(double amount,Unit unit, String name) {
        this.amount = amount;
       this.unit = unit;
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString ()
    {
        if(amount==0)
        {
            return unit.toString()+" "+name;
        }
        return amount.toString() + " " +unit.toString()+ " " + name;
    }
    public int getUnitInt()
    {
        return this.unit.getInt();
    }


}
