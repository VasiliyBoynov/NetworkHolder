package Client.Commands;

import java.nio.charset.StandardCharsets;
import java.util.List;

    public abstract class Instruction {
        public Commands Instruction;

        public Instruction(Commands instruction) {
            Instruction = instruction;
        }

        public String getInstruction() {
            return Instruction.toString();
        }

        public abstract List<String> toJSON();

        public byte[] objToByte() {
            StringBuilder str = new StringBuilder();
            str.append((byte) 1);
            for (String s : toJSON()) {
                str.append(s);
            }
            return str.toString().getBytes(StandardCharsets.UTF_8);
        }


    }

