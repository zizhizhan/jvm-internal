package com.zizhizhan.legacy.pattern.bridge;

class DerivedAbstraction_One extends Abstraction {
    @Override
    public void DumpString(String str) {
        str += ".com";
        impToUse.DoStringOp(str);
    }
}
