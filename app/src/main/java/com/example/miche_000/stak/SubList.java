package com.example.miche_000.stak;

import java.util.ArrayList;

/**
 * Created by miche_000 on 7/26/2017.
 */

public class SubList {
    ArrayList<Sub> list;

    public SubList(){
        list = new ArrayList<Sub>();
    }

    public String getAfter(String s){
        for(Sub a: list){
            if(a.subreddit.equals(s))
                return a.after;
        }
        return "notIn";
    }

    public void setAfter(String s, String a){
        boolean alreadyIn = false;
        for(Sub l: list){
            if(l.subreddit !=null && l.subreddit.equals(s)) {
                alreadyIn = true;
                l.setAfter(a);
                break;
            }
        }
        if(!alreadyIn)
            list.add(new Sub(s,a));
    }

    public void add(String s, String a){
        boolean alreadyIn = false;
        for(Sub l: list){
            if(l.subreddit.equals(s))
                alreadyIn = true;
        }

        if(!alreadyIn)
            list.add(new Sub(s,a));
    }


}
