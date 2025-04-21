package wasm.misc;

import wasm.disassembly.instructions.Instr;
import wasm.disassembly.instructions.InstrType;
import wasm.disassembly.modules.indices.TypeIdx;
import wasm.disassembly.modules.sections.code.Func;
import wasm.disassembly.types.FuncType;

import java.util.ArrayList;
import java.util.List;

public abstract class StreamReplacement {

    /**
     * The index of the function type in the type section.
     */
    private TypeIdx typeIdx;

    /**
     * The list of function indices that match this replacement type.
     */
    private final List<Integer> functions;

    private boolean isPatched;

    protected boolean patchOnlyOnce = true;

    protected StreamReplacement() {
        this.functions = new ArrayList<>();
    }

    public TypeIdx getTypeIdx() {
        return typeIdx;
    }

    public void setTypeIdx(TypeIdx typeIdx) {
        this.typeIdx = typeIdx;
    }

    public void addFunction(final int funcId) {
        this.functions.add(funcId);
    }

    public boolean hasFunction(final int funcId) {
        return this.functions.contains(funcId);
    }

    public boolean isPatched() {
        return isPatched;
    }

    public void setPatched(boolean patched) {
        if (isPatched && patchOnlyOnce) {
            throw new IllegalStateException("Cannot patch more than once");
        }

        isPatched = patched;
    }

    public abstract FuncType getFuncType();

    public abstract ReplacementType getReplacementType();

    public abstract String getImportName();

    public abstract String getExportName();

    public abstract boolean codeMatches(int id, Func code);

    public enum ReplacementType {
        HOOK,
        HOOK_COPYEXPORT,
        HOOK_DEBUG,
    }

    protected static boolean codeStartsWith(final List<Instr> instructions, final List<InstrType> expected) {
        if (instructions.size() < expected.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            if (instructions.get(i).getInstrType() != expected.get(i)) {
                return false;
            }
        }

        return true;
    }

    protected static boolean codeEquals(final List<Instr> instructions, final List<InstrType> expected) {
        if (instructions.size() != expected.size()) {
            return false;
        }

        for (int i = 0; i < expected.size(); i++) {
            if (instructions.get(i).getInstrType() != expected.get(i)) {
                return false;
            }
        }

        return true;
    }

}
