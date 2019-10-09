package wrapperservice;

public class ExceptionWrapper{

    static class NotValidUri extends RuntimeException {
        NotValidUri(String uri){ super("Not an valid URI:  " + uri);
        }
    }
    static class UriNotProvided extends RuntimeException {
        UriNotProvided(){
            super("URI not provided!");
        }
    }
    static class UnknownHost extends RuntimeException {
        UnknownHost(){
            super("Unknown Host provided!");
        }
    }
}
