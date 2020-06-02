package me.rvbiljouw.awsum.response;

/**
 * @author rvbiljouw
 */
public class MessageWrapper<T> {
    private String type;
    private T body;

    public MessageWrapper(String type, T body) {
        this.type = type;
        this.body = body;
    }

    public MessageWrapper(T body) {
        this.type = body.getClass().getSimpleName();
        this.body = body;
    }

    public MessageWrapper() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public static <T> MessageWrapper<T> wrap(T entity) {
        return new MessageWrapper<>(entity);
    }
}
