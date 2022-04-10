package p01simple;

import lwjglutils.OGLBuffers;
import lwjglutils.ToIntArray;

import java.util.ArrayList;
import java.util.List;

public class GridFactory {

    /**
     * @param a počet vrcholů v řádku
     * @param b počet vrcholů ve sloupci
     * @return
     */
    public static OGLBuffers generateGrid(int a, int b) {
        float[] vb = new float[a * b * 2];
        int indexVB = 0;
        for (int i = 0; i < b; i++) {
            for (int j = 0; j < a; j++) {
                vb[indexVB++] = j / (float) (a - 1);
                vb[indexVB++] = i / (float) (b - 1);
            }
        }

        int[] ib = new int[(a - 1) * (b - 1) * 2 * 3];
        int indexIB = 0;
        for (int i = 0; i < b - 1; i++) {
            int row = i * a;
            for (int j = 0; j < a - 1; j++) {
                ib[indexIB++] = j + row;
                ib[indexIB++] = j + a + row;
                ib[indexIB++] = j + 1 + row;

                ib[indexIB++] = j + a + row;
                ib[indexIB++] = j + a + 1 + row;
                ib[indexIB++] = j + 1 + row;
            }
        }

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };
        return new OGLBuffers(vb, attributes, ib);
    }

}
