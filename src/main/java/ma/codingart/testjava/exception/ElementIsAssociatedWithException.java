package ma.codingart.testjava.exception;

public class ElementIsAssociatedWithException extends BusinessException {

    private static final long serialVersionUID = 712719389442641868L;

    public ElementIsAssociatedWithException() {
        super();
    }

    public ElementIsAssociatedWithException(Throwable cause, String key, Object[] args) {
        super(cause, key, args);
    }

}
