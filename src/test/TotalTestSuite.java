package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  test.lcp.LcpPacketTest.class,
  test.piTransfer.FileHelperTest.class,
  test.berryPicker.FileObjectTest.class,
  test.lcp.ByteUtilsTest.class
})


public class TotalTestSuite {

}
