package Client.Commands;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Authorization  extends Instruction{

    private String nickName;
    private String password;

    public Authorization(Commands instruction) {
        super(instruction);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public List<String> toJSON() {
        List<String> list = new ArrayList<String>();
        list.add("{\n");
        list.add(String.format("\"%s\": \"%s\",%n","Instruction",getInstruction()));
        list.add(String.format("\"%s\": \"%s\",%n","nickName",getNickName()));
        list.add(String.format("\"%s\": \"%s\"%n","password",getPassword()));
        list.add("}\n");
        return list;
    }


}
