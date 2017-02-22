package com.tatsuo.baseballrecorder.aws;

/**
 * Created by tatsuo on 2017/02/04.
 */

public class S3Result {

    public static final int STATUS_NONE = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_COMPLETE = 2;
    public static final int STATUS_ERROR = 9;

    private int status;

    public S3Result(){
        status = STATUS_NONE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
