package Server;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientStatus {
    private AtomicBoolean autorization = new AtomicBoolean(false);


    public boolean getAutorization() {
        return autorization.get();
    }

    public void setAutorization(boolean autorization) {
        this.autorization.set(autorization);
    }

    public static boolean isComandUser(List<String> list){
        for (String s : list) {
            if ((s.indexOf("\"Instruction\"")!=-1
            & s.indexOf("\"user\"")!=-1)) return true;
        }
        return false;
    };
}
