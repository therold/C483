package Presentation;

import java.util.concurrent.Callable;
import javafx.scene.Node;

public class InputValidator implements Callable<Node> {
    private Node node;
    private boolean isValid;
    private String errorMessage;
    private String errorMessageBody;
    
    public InputValidator(Node node, boolean isValid, String errorMessage, String errorMessageBody) {
        this.node = node;
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.errorMessageBody = errorMessageBody;
    }
    
    @Override
    public Node call() throws InvalidInputException {
        if (!isValid) {
            throw new InvalidInputException(node, errorMessage, errorMessageBody);
        }
        return node;
    }
}
