package nl.nhl.software_development.controller.net;

import com.google.gson.annotations.SerializedName;

public class Hello
{
    @SerializedName("Message")
    private String message;

    public Hello()
    {
        message = "";
    }

    public Hello(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
