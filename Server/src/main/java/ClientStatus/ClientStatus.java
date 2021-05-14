package ClientStatus;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientStatus {
    //private AtomicBoolean startAutorization = new AtomicBoolean(false);
    private AtomicBoolean autorization = new AtomicBoolean(false);
    private String nickName;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }



    public boolean getAutorization() {
        return autorization.get();
    }

    public void setAutorization(boolean autorization) {
        this.autorization = new AtomicBoolean(autorization);
    }
}
