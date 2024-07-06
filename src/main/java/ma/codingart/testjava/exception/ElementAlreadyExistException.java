package ma.codingart.testjava.exception;

public class ElementAlreadyExistException extends BusinessException {

    private static final long serialVersionUID = 712719389442641868L;

    public ElementAlreadyExistException() {
        super();
    }

    public ElementAlreadyExistException(Throwable cause, String key, Object[] args) {
        super(cause, key, args);
    }

}
