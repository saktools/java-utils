package io.github.saktools.utils.http;

public class HttpJsonResponse<T> {

    private Boolean success;

    private Integer code;

    private String message;

    private T data;



    public static <T> HttpJsonResponse<T> success(T data) {
        return success(data, "", 200);
    }

    public static <T> HttpJsonResponse<T> success(T data, String message) {
        return success(data, message, 200);
    }

    public static <T> HttpJsonResponse<T> success(T data, String message, Integer code) {
        HttpJsonResponse httpJsonResponse = new HttpJsonResponse<T>();
        httpJsonResponse.setSuccess(true);
        httpJsonResponse.setCode(code);
        httpJsonResponse.setData(data);
        httpJsonResponse.setMessage(message);
        return httpJsonResponse;
    }


    public static <T> HttpJsonResponse<T> error(String message) {
        return error(message, 500, null);
    }

    public static <T> HttpJsonResponse<T> error(String message, Integer code) {
        return error(message, code, null);
    }

    public static <T> HttpJsonResponse<T> error(String message, Integer code, T data) {
        HttpJsonResponse httpJsonResponse = new HttpJsonResponse<T>();
        httpJsonResponse.setSuccess(false);
        httpJsonResponse.setCode(code);
        httpJsonResponse.setData(data);
        httpJsonResponse.setMessage(message);
        return httpJsonResponse;
    }


    @Override
    public String toString() {
        return "HttpJsonResponse{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
