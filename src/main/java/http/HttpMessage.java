package http;

public abstract class HttpMessage {

    private HttpMethod method;
    private String requestTarget;
    private String httpVersion;

    public HttpMessage() {}

    // GETTERS AND SETTERS
    public HttpMethod getMethod() {
        return method;
    }

     void setMethod(HttpMethod method) {
        this.method = method;
    }
}
