package wrapperservice;

public class ExceptionWrapper{

    static class NotValidUri extends RuntimeException {
        NotValidUri(String uri){
            super("Not an valid URI:  " + uri);
        }
    }
    static class UriNotProvided extends RuntimeException {
        UriNotProvided(){
            super("URI not provided!");
        }
    }
}
