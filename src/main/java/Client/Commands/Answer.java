package Client.Commands;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Answer {
    private Commands instruction;
    private AtomicBoolean rezl = new AtomicBoolean(false);

    public Answer (List<String> list){
        for (String s : list) {
            if (instruction==null){
                for (Commands commands : Commands.values()) {
                    if(s.indexOf(commands.toString())!=-1){
                        this.instruction=commands;
                        break;
                    }
                }
            }
            if(s.indexOf("\"rezl\"")!=-1){
                rezl.set(s.indexOf("\"true\"")!=-1);
            }
        }
    }

    public Commands getInstruction() {
        return instruction;
    }

    public AtomicBoolean getRezl() {
        return rezl;
    }
}
