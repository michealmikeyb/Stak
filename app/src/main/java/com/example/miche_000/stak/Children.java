package com.example.miche_000.stak;

/**
 * Created by miche_000 on 7/25/2017.
 */

public class Children {
    private Data data;

    private String kind;

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getKind ()
    {
        return kind;
    }

    public void setKind (String kind)
    {
        this.kind = kind;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", kind = "+kind+"]";
    }
}
