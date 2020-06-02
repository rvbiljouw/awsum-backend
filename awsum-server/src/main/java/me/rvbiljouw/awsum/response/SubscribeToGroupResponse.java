package me.rvbiljouw.awsum.response;

/**
 * @author rvbiljouw
 */
public class SubscribeToGroupResponse {
    private boolean success;

    public SubscribeToGroupResponse(boolean success) {
        this.success = success;
    }

    public SubscribeToGroupResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
