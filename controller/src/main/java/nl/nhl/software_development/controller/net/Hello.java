package nl.nhl.software_development.controller.net;

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
