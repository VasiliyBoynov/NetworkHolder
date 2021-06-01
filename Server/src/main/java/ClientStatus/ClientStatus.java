package ClientStatus;

import Commands.SetFile;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientStatus {
    //private AtomicBoolean startAutorization = new AtomicBoolean(false);
    private AtomicBoolean autorization = new AtomicBoolean(false);
    private String nickName;
    private int idUser;

    private String nameFileToServer;
    private AtomicBoolean startSetFile = new AtomicBoolean(false);
    private SetFile setFileMetaData;

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public SetFile getSetFileMetaData() {
        return setFileMetaData;
    }

    public void setSetFileMetaData(SetFile setFileMetaData) {
        this.setFileMetaData = setFileMetaData;
    }

    public String getNameFileToServer() {
        return nameFileToServer;
    }

    public void setNameFileToServer(String nameFileToServer) {
        this.nameFileToServer = nameFileToServer;
    }

    public AtomicBoolean getStartSetFile() {
        return startSetFile;
    }

    public void setStartSetFile(AtomicBoolean startSetFile) {
        this.startSetFile = startSetFile;
    }

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
