package Commands;

import java.util.concurrent.atomic.AtomicBoolean;

public class Answer extends Message{
    private String typeMessage;
    private boolean rezl;
    private String text;

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public boolean isRezl() {
        return rezl;
    }

    public void setRezl(boolean rezl) {
        this.rezl = rezl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("typeMessage: %s, rezl: %s, txt: %s",typeMessage,rezl,text);
    }
}
