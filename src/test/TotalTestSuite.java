package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  test.lcp.LcpPacketTest.class,
  test.piTransfer.FileManagerTest.class
})


public class TotalTestSuite {

}