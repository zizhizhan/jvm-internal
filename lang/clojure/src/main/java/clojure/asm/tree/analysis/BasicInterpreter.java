/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package clojure.asm.tree.analysis;

import java.util.List;

import clojure.asm.tree.InvokeDynamicInsnNode;

/**
 * An {@link Interpreter} for {@link BasicValue} values.
 * 
 * @author Eric Bruneton
 * @author Bing Ran
 */
public class BasicInterpreter extends Interpreter<BasicValue> implements
        clojure.asm.Opcodes {

    public BasicInterpreter() {
        super(ASM4);
    }

    protected BasicInterpreter(final int api) {
        super(api);
    }

    @Override
    public BasicValue newValue(final clojure.asm.Type type) {
        if (type == null) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        switch (type.getSort()) {
        case clojure.asm.Type.VOID:
            return null;
        case clojure.asm.Type.BOOLEAN:
        case clojure.asm.Type.CHAR:
        case clojure.asm.Type.BYTE:
        case clojure.asm.Type.SHORT:
        case clojure.asm.Type.INT:
            return BasicValue.INT_VALUE;
        case clojure.asm.Type.FLOAT:
            return BasicValue.FLOAT_VALUE;
        case clojure.asm.Type.LONG:
            return BasicValue.LONG_VALUE;
        case clojure.asm.Type.DOUBLE:
            return BasicValue.DOUBLE_VALUE;
        case clojure.asm.Type.ARRAY:
        case clojure.asm.Type.OBJECT:
            return BasicValue.REFERENCE_VALUE;
        default:
            throw new Error("Internal error");
        }
    }

    @Override
    public BasicValue newOperation(final clojure.asm.tree.AbstractInsnNode insn)
            throws AnalyzerException {
        switch (insn.getOpcode()) {
        case ACONST_NULL:
            return newValue(clojure.asm.Type.getObjectType("null"));
        case ICONST_M1:
        case ICONST_0:
        case ICONST_1:
        case ICONST_2:
        case ICONST_3:
        case ICONST_4:
        case ICONST_5:
            return BasicValue.INT_VALUE;
        case LCONST_0:
        case LCONST_1:
            return BasicValue.LONG_VALUE;
        case FCONST_0:
        case FCONST_1:
        case FCONST_2:
            return BasicValue.FLOAT_VALUE;
        case DCONST_0:
        case DCONST_1:
            return BasicValue.DOUBLE_VALUE;
        case BIPUSH:
        case SIPUSH:
            return BasicValue.INT_VALUE;
        case LDC:
            Object cst = ((clojure.asm.tree.LdcInsnNode) insn).cst;
            if (cst instanceof Integer) {
                return BasicValue.INT_VALUE;
            } else if (cst instanceof Float) {
                return BasicValue.FLOAT_VALUE;
            } else if (cst instanceof Long) {
                return BasicValue.LONG_VALUE;
            } else if (cst instanceof Double) {
                return BasicValue.DOUBLE_VALUE;
            } else if (cst instanceof String) {
                return newValue(clojure.asm.Type.getObjectType("java/lang/String"));
            } else if (cst instanceof clojure.asm.Type) {
                int sort = ((clojure.asm.Type) cst).getSort();
                if (sort == clojure.asm.Type.OBJECT || sort == clojure.asm.Type.ARRAY) {
                    return newValue(clojure.asm.Type.getObjectType("java/lang/Class"));
                } else if (sort == clojure.asm.Type.METHOD) {
                    return newValue(clojure.asm.Type
                            .getObjectType("java/lang/invoke/MethodType"));
                } else {
                    throw new IllegalArgumentException("Illegal LDC constant "
                            + cst);
                }
            } else if (cst instanceof clojure.asm.Handle) {
                return newValue(clojure.asm.Type
                        .getObjectType("java/lang/invoke/MethodHandle"));
            } else {
                throw new IllegalArgumentException("Illegal LDC constant "
                        + cst);
            }
        case JSR:
            return BasicValue.RETURNADDRESS_VALUE;
        case GETSTATIC:
            return newValue(clojure.asm.Type.getType(((clojure.asm.tree.FieldInsnNode) insn).desc));
        case NEW:
            return newValue(clojure.asm.Type.getObjectType(((clojure.asm.tree.TypeInsnNode) insn).desc));
        default:
            throw new Error("Internal error.");
        }
    }

    @Override
    public BasicValue copyOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final BasicValue value) throws AnalyzerException {
        return value;
    }

    @Override
    public BasicValue unaryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final BasicValue value) throws AnalyzerException {
        switch (insn.getOpcode()) {
        case INEG:
        case IINC:
        case L2I:
        case F2I:
        case D2I:
        case I2B:
        case I2C:
        case I2S:
            return BasicValue.INT_VALUE;
        case FNEG:
        case I2F:
        case L2F:
        case D2F:
            return BasicValue.FLOAT_VALUE;
        case LNEG:
        case I2L:
        case F2L:
        case D2L:
            return BasicValue.LONG_VALUE;
        case DNEG:
        case I2D:
        case L2D:
        case F2D:
            return BasicValue.DOUBLE_VALUE;
        case IFEQ:
        case IFNE:
        case IFLT:
        case IFGE:
        case IFGT:
        case IFLE:
        case TABLESWITCH:
        case LOOKUPSWITCH:
        case IRETURN:
        case LRETURN:
        case FRETURN:
        case DRETURN:
        case ARETURN:
        case PUTSTATIC:
            return null;
        case GETFIELD:
            return newValue(clojure.asm.Type.getType(((clojure.asm.tree.FieldInsnNode) insn).desc));
        case NEWARRAY:
            switch (((clojure.asm.tree.IntInsnNode) insn).operand) {
            case T_BOOLEAN:
                return newValue(clojure.asm.Type.getType("[Z"));
            case T_CHAR:
                return newValue(clojure.asm.Type.getType("[C"));
            case T_BYTE:
                return newValue(clojure.asm.Type.getType("[B"));
            case T_SHORT:
                return newValue(clojure.asm.Type.getType("[S"));
            case T_INT:
                return newValue(clojure.asm.Type.getType("[I"));
            case T_FLOAT:
                return newValue(clojure.asm.Type.getType("[F"));
            case T_DOUBLE:
                return newValue(clojure.asm.Type.getType("[D"));
            case T_LONG:
                return newValue(clojure.asm.Type.getType("[J"));
            default:
                throw new AnalyzerException(insn, "Invalid array type");
            }
        case ANEWARRAY:
            String desc = ((clojure.asm.tree.TypeInsnNode) insn).desc;
            return newValue(clojure.asm.Type.getType("[" + clojure.asm.Type.getObjectType(desc)));
        case ARRAYLENGTH:
            return BasicValue.INT_VALUE;
        case ATHROW:
            return null;
        case CHECKCAST:
            desc = ((clojure.asm.tree.TypeInsnNode) insn).desc;
            return newValue(clojure.asm.Type.getObjectType(desc));
        case INSTANCEOF:
            return BasicValue.INT_VALUE;
        case MONITORENTER:
        case MONITOREXIT:
        case IFNULL:
        case IFNONNULL:
            return null;
        default:
            throw new Error("Internal error.");
        }
    }

    @Override
    public BasicValue binaryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final BasicValue value1, final BasicValue value2)
            throws AnalyzerException {
        switch (insn.getOpcode()) {
        case IALOAD:
        case BALOAD:
        case CALOAD:
        case SALOAD:
        case IADD:
        case ISUB:
        case IMUL:
        case IDIV:
        case IREM:
        case ISHL:
        case ISHR:
        case IUSHR:
        case IAND:
        case IOR:
        case IXOR:
            return BasicValue.INT_VALUE;
        case FALOAD:
        case FADD:
        case FSUB:
        case FMUL:
        case FDIV:
        case FREM:
            return BasicValue.FLOAT_VALUE;
        case LALOAD:
        case LADD:
        case LSUB:
        case LMUL:
        case LDIV:
        case LREM:
        case LSHL:
        case LSHR:
        case LUSHR:
        case LAND:
        case LOR:
        case LXOR:
            return BasicValue.LONG_VALUE;
        case DALOAD:
        case DADD:
        case DSUB:
        case DMUL:
        case DDIV:
        case DREM:
            return BasicValue.DOUBLE_VALUE;
        case AALOAD:
            return BasicValue.REFERENCE_VALUE;
        case LCMP:
        case FCMPL:
        case FCMPG:
        case DCMPL:
        case DCMPG:
            return BasicValue.INT_VALUE;
        case IF_ICMPEQ:
        case IF_ICMPNE:
        case IF_ICMPLT:
        case IF_ICMPGE:
        case IF_ICMPGT:
        case IF_ICMPLE:
        case IF_ACMPEQ:
        case IF_ACMPNE:
        case PUTFIELD:
            return null;
        default:
            throw new Error("Internal error.");
        }
    }

    @Override
    public BasicValue ternaryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final BasicValue value1, final BasicValue value2,
            final BasicValue value3) throws AnalyzerException {
        return null;
    }

    @Override
    public BasicValue naryOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final List<? extends BasicValue> values) throws AnalyzerException {
        int opcode = insn.getOpcode();
        if (opcode == MULTIANEWARRAY) {
            return newValue(clojure.asm.Type.getType(((clojure.asm.tree.MultiANewArrayInsnNode) insn).desc));
        } else if (opcode == INVOKEDYNAMIC) {
            return newValue(clojure.asm.Type
                    .getReturnType(((InvokeDynamicInsnNode) insn).desc));
        } else {
            return newValue(clojure.asm.Type.getReturnType(((clojure.asm.tree.MethodInsnNode) insn).desc));
        }
    }

    @Override
    public void returnOperation(final clojure.asm.tree.AbstractInsnNode insn,
            final BasicValue value, final BasicValue expected)
            throws AnalyzerException {
    }

    @Override
    public BasicValue merge(final BasicValue v, final BasicValue w) {
        if (!v.equals(w)) {
            return BasicValue.UNINITIALIZED_VALUE;
        }
        return v;
    }
}
