package com.example.lenovo.myrecipecollection.ourUtilities;

/**
 * Created by Dima on 05/09/2015.
 */
public class FreeMemory implements Runnable{

    @Override
    public void run() {
        System.gc();
    }
}
