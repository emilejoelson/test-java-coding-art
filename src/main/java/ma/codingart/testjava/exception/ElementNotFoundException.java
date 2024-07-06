package ma.codingart.testjava.exception;
public class ElementNotFoundException extends BusinessException {

    private static final long serialVersionUID = 3514588905105588434L;

    public ElementNotFoundException() {
        super();
    }

    public ElementNotFoundException(Throwable cause, String key, Object[] args) {
        super(cause, key, args);
    }

}

