package pl.travelagency.api;

public class ResponseBase<T> {
    private T data;
    private ErrorModel error;

    public static <T> ResponseBase<T> ok(T data) {
        var r = new ResponseBase<T>();
        r.data = data;
        return r;
    }

    public static <T> ResponseBase<T> fail(String errorType) {
        var r = new ResponseBase<T>();
        r.error = new ErrorModel(errorType);
        return r;
    }

    public T getData() { return data; }
    public ErrorModel getError() { return error; }
}