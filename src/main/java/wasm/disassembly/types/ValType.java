package wasm.disassembly.types;

import java.util.HashMap;
import java.util.Map;

public enum ValType {
    I32(0x7f),
    I64(0x7e),
    F32(0x7d),
    F64(0x7c),
    REF_FUNC(0x70),
    REF_EXTERN(0x6F);


    public int val;
    ValType(int val) {
        this.val = val;
    }

    private static Map<Integer, ValType> map = new HashMap<>();
    static {
        for (ValType valType : ValType.values()) {
            map.put(valType.val, valType);
        }
    }

    public static ValType from_val(int val) {
        final ValType valType = map.get(val);
        if (valType == null) {
            throw new IllegalArgumentException(String.format("Invalid ValType 0x%x", val));
        }
        return valType;
    }

}
