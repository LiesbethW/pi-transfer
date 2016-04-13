package connection.lcp;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {
    
    public static byte[] longToBytes(long x) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
    	longBuffer.clear();
        longBuffer.putLong(0, x);
        return longBuffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
        longBuffer.clear();
    	longBuffer.put(bytes, 0, bytes.length);
        longBuffer.flip();//need flip 
        return longBuffer.getLong();
    }
    
    public static byte[] shortToBytes(short x) {
    	ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
    	shortBuffer.clear();
    	shortBuffer.putShort(x);
    	return shortBuffer.array();
    }
    
    public static short bytesToShort(byte[] bytes) {
    	ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
    	shortBuffer.clear();
    	shortBuffer.put(bytes, 0, bytes.length);
    	shortBuffer.flip();
    	return shortBuffer.getShort();
    }
    
    public static byte[] intToThreeBytes(int x) {
    	ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
    	intBuffer.clear();
    	intBuffer.putInt(x);
    	return Arrays.copyOfRange(intBuffer.array(), 1, 4);
    }
    
    public static int threeBytesToInt(byte[] bytes) {
    	ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
    	intBuffer.clear();
    	intBuffer.put((byte) 0);
    	intBuffer.put(bytes, 0, bytes.length);
        intBuffer.flip();//need flip 
        return intBuffer.getInt();
    }
    
    public static byte[] longToFourBytes(long x) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
    	longBuffer.clear();
        longBuffer.putLong(0, x);
        byte[] fourBytes = new byte[4];
        System.arraycopy(longBuffer.array(), 4, fourBytes, 0, 4);
        return fourBytes;
    }
    
    public static long fourBytesToLong(byte[] bytes) {
    	ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
        longBuffer.clear();
    	byte[] paddingbytes = new byte[4];
    	longBuffer.put(paddingbytes);
    	longBuffer.put(bytes, 0, bytes.length);
        longBuffer.flip();//need flip 
        return longBuffer.getLong();
    }
}