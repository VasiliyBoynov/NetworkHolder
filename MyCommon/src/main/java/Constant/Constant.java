package Constant;

public enum Constant {

    MAX_PACKAGE_BYTE(40000),
    TIME_OUT_S(600),
    INITIAL_BYTE_TO_STRIP(2),
    MAX_FRAME_LENGTH(65536);

    private int constant;

    Constant(int constant) {
        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }


}
