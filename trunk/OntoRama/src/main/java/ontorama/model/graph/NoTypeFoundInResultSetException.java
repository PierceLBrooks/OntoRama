package ontorama.model.graph;

@SuppressWarnings("serial")
public class NoTypeFoundInResultSetException extends InvalidArgumentException {

    public NoTypeFoundInResultSetException(String termName) {
        super("term '" + termName + "' not found in Query Result Set");
    }
}