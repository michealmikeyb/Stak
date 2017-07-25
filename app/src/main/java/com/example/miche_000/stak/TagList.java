package com.example.miche_000.stak;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by miche_000 on 7/24/2017.
 */

public class TagList {
    PersonalTag[] list;
    ArrayList<PersonalTag> allTags;
    TagDeficit def;

    public TagList(){
        allTags = new ArrayList<PersonalTag>();
        list = new PersonalTag[10000];
        PersonalTag popular = new PersonalTag("popular");

        for(int i = 0; i<10000; i++){
            list[i] = popular;
        }

        allTags.add(popular);

        int[] deflist = new int[10000];

        int j = 0;
        for(int i : deflist){
            deflist[j] = j;
            j++;
        }
        def = new TagDeficit(100, deflist);



    }

    public void like(PersonalTag p){
        boolean alreadyIn = false;
        for(PersonalTag s: allTags){
            if(s.name.equals(p.name)){
                alreadyIn = true;
            }
        }
        double raise;
        if(alreadyIn){
            raise = p.like();
        }
        else {
            raise = p.firstLike();
            allTags.add(p);

        }
        if(def.deficit>0) {
            if (raise < def.deficit) {
                int[] numbers = def.give((int) (100 * raise));
                p.addNumbers(numbers, alreadyIn);

                for (int i : numbers) {
                    list[i] = p;
                }
            } else {
                int newRaise = (int) (def.deficit) * 100;
                int[] numbers = def.give(newRaise);
                p.addNumbers(numbers, alreadyIn);
                for (int i : numbers) {
                    list[i] = p;
                }
            }
        }
    }

    public void dislike(PersonalTag p){
        boolean alreadyIn = false;
        for(PersonalTag s: allTags){
            if(s.name.equals(p.name)){
                alreadyIn = true;
            }
        }
        double deficit;
        if(alreadyIn) {
            deficit = p.dislike();



            if (p.isNegative()) {
                p.rating -= p.dislike();

            }

            else {

                if (def.deficit - deficit < 10000) {
                    int[] nums = p.takeNumbers((int) (deficit * 100));
                    def.take(nums);

                }
                else {
                    int newDeficit = (int) (10000 - def.deficit);
                    def.take(p.takeNumbers(newDeficit));
                }
            }
        }

        else {
            p.firstDislike();
            allTags.add(p);
        }

    }

    public String getTag(){
        Random generator = new Random();
        int number = generator.nextInt(10000);


        return list[number].name;
    }

    public String toString(){
        String r = "";
        for(PersonalTag t : allTags){
            r+=(t.toString()+"\n");
        }
        return r;
    }

    public String getList(){
        String r = "";
        for(PersonalTag t : list){
            r+=(t.toString()+"\n");
        }
        return r;
    }

    public PersonalTag[] getArrayList(){
        return list;
    }
}
