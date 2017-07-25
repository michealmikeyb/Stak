package com.example.miche_000.stak;

import java.util.ArrayList;

/**
 * Created by miche_000 on 7/24/2017.
 */

public class PersonalTag {
    public double rating;
    public double LikeMultiplier;
    public double DislikeMultiplier;
    public String name;
    ArrayList<Integer> listNumbers;


    public PersonalTag(String n){
        name = n;
        rating = 0;
        LikeMultiplier = 0.5;
        DislikeMultiplier = 0.5;
        listNumbers = new ArrayList<Integer>();

    }

    public boolean isNegative(){
        return rating<0;
    }

    public int firstLike(){
        rating = 5;
        LikeMultiplier = 0.6;
        DislikeMultiplier = 0.4;
        return 5;

    }
    public void addNumbers(int[] i, boolean alreadyIn){
        for(Integer j: i){
            listNumbers.add(j);
        }
        if(alreadyIn) {
            rating = rating+ ((double)i.length / 100);
        }

    }

    public int[] takeNumbers( int x) {
        int numberOfPlaces = (int)Math.floor(rating*100);
        int[] removed = new int[x];
        if(x<numberOfPlaces) {
            for (int i = x; i > 0; i--) {
                removed[i - 1] = listNumbers.remove((numberOfPlaces - 1));
                numberOfPlaces--;
            }
            rating = (double) numberOfPlaces / 100;
            return removed;
        }
        else{
            for (int i = numberOfPlaces; i > 0; i--) {
                removed[i - 1] = listNumbers.remove((numberOfPlaces - 1));
                numberOfPlaces--;
            }
            rating = (double) numberOfPlaces / 100;
            return removed;
        }
    }

    public void addToList(){

    }

    public int firstDislike(){
        rating = -5;
        DislikeMultiplier = 0.6;
        LikeMultiplier = 0.4;
        return -5;
    }

    public double like(){
        double raise = (1*LikeMultiplier);
        LikeMultiplier = LikeMultiplier+0.1;
        DislikeMultiplier = 1-LikeMultiplier;
        return raise;
    }

    public double dislike(){
        double deficit = (1*DislikeMultiplier);
        DislikeMultiplier = DislikeMultiplier + 0.1;
        LikeMultiplier = 1-LikeMultiplier;
        return deficit;
    }

    public String toString(){
        String r = "tag name: "+name+ " tag rating: "+ rating;
        return r;
    }
}
