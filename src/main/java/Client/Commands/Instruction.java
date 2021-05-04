package Client.Commands;

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

        public abstract byte[] objToByte();


    }

