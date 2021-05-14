package DB;

public class MessageDB{
    private boolean rezl;
    private String txt;

    public MessageDB(boolean rezl, String txt) {
        this.rezl = rezl;
        this.txt = txt;
    }

    public MessageDB() {}

    public boolean isRezl() {
        return rezl;
    }

    public void setRezl(boolean rezl) {
        this.rezl = rezl;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    @Override
    public String toString() {
        return String.format("rezl : %s, txt: %s",rezl,txt);
    }
}
