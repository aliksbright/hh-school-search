package hhYandexTest;

import java.util.Arrays;

public class VarByte {

    public static byte [] longToVarByte(long v) {
        byte [] temp = new byte[10];
        int len;

        if (v == 0)
            return new byte[] {0};

        temp[0] = 0;
        len = 0;
        while (v != 0) {
            temp[len] = (byte) (v & 0x7F);
            if (len > 0)
                temp[len] |= 0x80;
            v >>>= 7;
            len++;
        }

        byte [] result;
        result = (len < temp.length) ? Arrays.copyOf(temp, len) : temp;

        for (int n =0 ; n < result.length / 2; n++) {
            byte tmp = result[n];
            result[n] = result[result.length - n - 1];
            result[result.length - n - 1] = tmp;
        }

        return result;
    }

}
