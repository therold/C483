package Presentation;

import javafx.scene.Node;

public class InvalidInputException extends Exception {
    private Node node;
    private String errorMessage;
    private String errorMessageBody;
    
    public InvalidInputException(Node node, String errorMessage, String errorMessageBody) {
        super();
        this.node = node;
        this.errorMessage = errorMessage;
        this.errorMessageBody = errorMessageBody;
    }
    
    public Node getNode() {
        return node;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getErrorMessageBody() {
        return errorMessageBody;
    }
}