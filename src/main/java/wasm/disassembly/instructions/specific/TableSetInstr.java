package wasm.disassembly.instructions.specific;

import wasm.disassembly.InvalidOpCodeException;
import wasm.disassembly.instructions.Instr;
import wasm.disassembly.instructions.InstrType;
import wasm.disassembly.modules.Module;
import wasm.disassembly.values.WSignedLong;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TableSetInstr extends Instr {

    private long constValue;

    public TableSetInstr(BufferedInputStream in, InstrType instrType, Module module) throws IOException, InvalidOpCodeException {
        super(instrType);
        this.constValue = WSignedLong.read(in, 64);
    }

    @Override
    protected void assemble2(OutputStream out) throws IOException, InvalidOpCodeException {
        WSignedLong.write(constValue, out, 64);
    }

    public long getConstValue() {
        return constValue;
    }

    public void setConstValue(long constValue) {
        this.constValue = constValue;
    }
}
