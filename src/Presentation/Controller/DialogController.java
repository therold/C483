package Presentation.Controller;

public interface DialogController<T> {
    public void postInit(T t);
    public T process();
    public void handleCancel();
    public void handleSave();
    public void checkCanSave();
}