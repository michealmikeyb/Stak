package com.example.miche_000.stak;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text1);

        PersonalTag pictures = new PersonalTag("pics");
        PersonalTag movies = new PersonalTag("movies");
        PersonalTag music = new PersonalTag("music");

        TagList list = new TagList();

        list.like(pictures);
        list.like(pictures);
        list.like(movies);
        list.dislike(music);
        list.dislike(movies);
        list.like(pictures);

        for(int i = 0; i<100; i++)
        list.like(pictures);

        for(int i = 0; i<4;i++)
            list.dislike(movies);
        list.like(pictures);

        int movieCount = 0;
        int picCount = 0;

        for(PersonalTag p: list.getArrayList()){
            if(p.name.equals("pics"))
                picCount++;
            else if(p.name.equals("movies"))
                    movieCount++;
        }
        text.setText("movies: "+movieCount+"pics: "+ picCount);


    }
}
