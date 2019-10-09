package wrapperservice;

public class StatusResponse {

    private final String uri;
    private final Boolean reachable;
    private final Integer error_code;
    private final String error_message;

    public StatusResponse(String uri, Boolean reachable, Integer error_code, String error_message){
        this.uri = uri;
        this.reachable = reachable;
        this.error_code = error_code;
        this.error_message = error_message;
     }

    public String getUri() {
        return uri;
    }

    public Boolean getReachable() {
        return reachable;
    }

    public Integer getError_code() {
        return error_code;
    }

    public String getError_message() {
        return error_message;
    }

    @Override
    public String toString() {
        return uri + " - " + reachable + " " + error_code + " " + error_message;
    }
}
