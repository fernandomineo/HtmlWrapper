package wrapperservice;

public class Uri {
    private static int id;
    private final String uri;

    public Uri(int id, String uri){
        this.id = id;
        this.uri = uri;
    }

    public static int getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }
}
