package DB;

import Commands.Info;

import java.util.ArrayList;

public class MessageDB{
    private boolean rezl;
    private String txt;
    private ArrayList<Info> list;

    public MessageDB(boolean rezl, String txt) {
        this.rezl = rezl;
        this.txt = txt;
    }

    public ArrayList<Info> getList() {
        return list;
    }

    public void setList(ArrayList<Info> list) {
        this.list = list;
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
