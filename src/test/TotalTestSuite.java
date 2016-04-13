package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  test.lcp.LcpPacketTest.class,
  test.filemanaging.FileHelperTest.class,
  test.berryPicker.FileObjectTest.class,
  test.lcp.ByteUtilsTest.class,
  test.lcp.LcpConnectionTest.class
})


public class TotalTestSuite {

}
