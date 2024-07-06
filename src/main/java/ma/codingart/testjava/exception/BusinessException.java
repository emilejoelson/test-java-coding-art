package ma.codingart.testjava.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String key;
    private Object[] args;
    public BusinessException() {
        super();
    }

    public BusinessException(final Throwable cause, final String key, final Object[] args) {
        super(cause);
        this.key = key;
        this.args = args;
    }

    public String getKey() {
        return key;
    }
    public Object[] getArgs() {
        return args;
    }


}
