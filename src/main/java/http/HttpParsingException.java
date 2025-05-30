package http;

public class HttpParsingException extends RuntimeException  {

    private final HttpStatusCode statusCode;

    public HttpParsingException(HttpStatusCode statusCode) {
        super(statusCode.MESSAGE);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
