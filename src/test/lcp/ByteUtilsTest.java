package test.lcp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import connection.lcp.ByteUtils;

public class ByteUtilsTest {
	
	
	@Test
	public void testLongToBytes() {
		long l = 34509678;
		byte[] bytes = ByteUtils.longToBytes(l);
		assertEquals(8, bytes.length);
		long l2 = ByteUtils.bytesToLong(bytes);
		assertEquals(l, l2);
	}
	
	@Test
	public void testShortToBytes() {
		short s = (short) 34670;
		byte[] bytes = ByteUtils.shortToBytes(s);
		assertEquals(2, bytes.length);
		short s2 = ByteUtils.bytesToShort(bytes);
		assertEquals(s, s2);
	}
	
	@Test
	public void testIntToThreeBytes() {
		int i = 2543978;
		byte[] bytes = ByteUtils.intToThreeBytes(i);
		assertEquals(3, bytes.length);
		int i2 = ByteUtils.threeBytesToInt(bytes);
		assertEquals(i, i2);
	}
	
	@Test
	public void testLongToFourBytes() {
		long l = 23415634;
		byte[] bytes = ByteUtils.longToFourBytes(l);
		assertEquals(4, bytes.length);
		long l2 = ByteUtils.fourBytesToLong(bytes);
		assertEquals(l, l2);
	}
	
}
