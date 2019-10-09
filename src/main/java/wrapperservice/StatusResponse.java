package wrapperservice;

public class StatusResponse {

    private final String uri;
    private final String reachable;
    private final String error_code;
    private final String error_message;

    public StatusResponse(String uri, String reachable, String error_code, String error_message){
        this.uri = uri;
        this.reachable = reachable;
        this.error_code = error_code;
        this.error_message = error_message;
     }

}
