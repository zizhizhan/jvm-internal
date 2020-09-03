package com.zizhizhan.legacies.pattern.filterchain.mina;


public class IdleStatus {
   
    public static final IdleStatus READER_IDLE = new IdleStatus("reader idle");
    public static final IdleStatus WRITER_IDLE = new IdleStatus("writer idle");
    public static final IdleStatus BOTH_IDLE = new IdleStatus("both idle");

    private final String strValue;

    private IdleStatus(String strValue) {
        this.strValue = strValue;
    }

    @Override
    public String toString() {
        return strValue;
    }
}
