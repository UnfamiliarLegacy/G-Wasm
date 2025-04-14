package wasm.disassembly.modules.sections.data;

import wasm.disassembly.InvalidOpCodeException;
import wasm.disassembly.instructions.numeric.NumericI32ConstInstr;
import wasm.disassembly.modules.Module;
import wasm.disassembly.modules.sections.Section;
import wasm.disassembly.values.WUnsignedInt;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataSection extends Section {

    public static final int DATA_SECTION_ID = 11;
    private final byte[] asBytes;
    private long length;
    private List<Data> dataSegments;

    public DataSection(BufferedInputStream in, Module module) throws IOException, InvalidOpCodeException {
        super(in, module, DATA_SECTION_ID);
        dataSegments = new ArrayList<>();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        length = WUnsignedInt.read(in, 32);
        for (int i = 0; i < length; i++) {
            final Data data = new Data(in, module);
            dataSegments.add(data);
            data.assemble(buffer);
        }
        asBytes = buffer.toByteArray();
    }

//    public DataSection(Module module, List<Data> dataSegments) {
//        super(module, DATA_SECTION_ID);
//        this.dataSegments = new Vector<>(dataSegments);
//    }

    @Override
    protected void assemble2(OutputStream out) throws IOException, InvalidOpCodeException {
//        dataSegments.assemble(out);

        WUnsignedInt.write(length, out, 32);
        out.write(asBytes);
    }

    public List<Data> getDataSegments() {
        return dataSegments;
    }

    public byte[] getData(int address, int length) {
        for (Data segment : dataSegments) {
            int start = ((NumericI32ConstInstr) segment.getOffset().getInstructions().get(0)).getConstValue();
            int end = start + segment.getData().length;

            if (address >= start && address < end) {
                final byte[] data = new byte[length];
                System.arraycopy(segment.getData(), address - start, data, 0, length);
                return data;
            }
        }

        return null;
    }

    public byte[] getDataBetween(int address, int end) {
        return getData(address, end - address);
    }

//    public void setDataSegments(List<Data> dataSegments) {
//        this.dataSegments = new Vector<>(dataSegments);
//    }
}
