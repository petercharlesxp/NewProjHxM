package com.NewApp;

/**
 * Created by Peter on 28/07/2015.
 */

public class Data
{
    private String id;

    private String HeartRate;

    private String InstantSpeed;

    private String posted;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getHeartRate ()
    {
        return HeartRate;
    }

    public void setHeartRate (String HeartRate)
    {
        this.HeartRate = HeartRate;
    }

    public String getInstantSpeed ()
    {
        return InstantSpeed;
    }

    public void setInstantSpeed (String InstantSpeed)
    {
        this.InstantSpeed = InstantSpeed;
    }

    public String getPosted ()
    {
        return posted;
    }

    public void setPosted (String posted)
    {
        this.posted = posted;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", HeartRate = "+HeartRate+", InstantSpeed = "+InstantSpeed+", posted = "+posted+"]";
    }
}


