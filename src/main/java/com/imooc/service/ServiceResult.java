package com.imooc.service;

public class ServiceResult<T> {
    private boolean success;
    private String message;
    private T result;

    public ServiceResult(boolean success) {
        this.success = success;
    }

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }
    public static <T> ServiceResult<T> notFound(){
        return new ServiceResult<>(false,Message.NOT_FOUND.getValue());
    }

    public static <T> ServiceResult<T> ofSuccess(){ return new ServiceResult<>(true);}

    public static <T> ServiceResult<T> of(T dto){
        ServiceResult<T> serviceResult = new ServiceResult<>(true);
        serviceResult.setResult(dto);
        return serviceResult;
    }

    public enum Message{
        NOT_FOUND("Not Found Resource!"),
        NOT_LOGIN("User Not Login!");
        private String value;

        Message(String value) {
            this.value=value;
        }

        public String getValue() {
            return value;
        }
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
