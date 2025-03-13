package exception;

import java.net.URI;

public class ErrorResponse {
    private String error;
    private int code;
    private URI url;

    public ErrorResponse(String error, int code, URI url) {
        this.error = error;
        this.code = code;
        this.url = url;
    }
}
