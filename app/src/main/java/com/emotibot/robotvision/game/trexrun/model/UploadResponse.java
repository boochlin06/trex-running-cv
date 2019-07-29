package com.emotibot.robotvision.game.trexrun.model;

public class UploadResponse {

    /**
     * request_id :
     * data :
     * error :
     */

    private String request_id;
    private String data;
    private String error;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
