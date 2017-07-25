package com.example.miche_000.stak;

/**
 * Created by miche_000 on 7/24/2017.
 */

public class TagDeficit {
    public double deficit;
    public int[] places;

    public TagDeficit(double d, int[] i){
        deficit = d;
        places = i;
    }

    public int[] give(int j){
        int numbers = (int)(Math.floor(deficit*100));
        int[] numToGive = new int[j];
        int x = 0;
        for(int i = numbers; i>numbers-j; i--){
            numToGive[x] = places[i-1];
            places[i-1] = 0;
            deficit-=0.01;
            x++;
        }
        return numToGive;
    }

    public void take(int[] j){
        int numbers = (int)Math.floor(deficit*100);
        for(int i: j){
            places[numbers] = i;
            numbers++;
        }
        deficit= ((double)numbers)/100;

    }
}
