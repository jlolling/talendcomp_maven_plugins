package de.cimt.talendcomp.dev.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cimt.talendcomp.dev.ComponentUtil;

public class TestComponentUtil {
	
	@Test
	public void testReadAndWrite() throws Exception {
		ComponentUtil util = new ComponentUtil();
		util.addJarFile("src/test/resources/jlo-talendcomp-json-13.2.jar", "de.jlo.talendcomp", "jlo-talendcomp-json", "13.2");
		util.setComponentBaseDir("src/test/components/");
		util.setComponentName("tTest");
		util.setComponentVersion("1.0");
		util.setUseTalendLibrariesMavenLocation(false);
		util.readXmlConfiguration();
		util.clearComponentJars();
		util.copyJars();
		util.setupXMLImports(false, null);
		util.setupXMLReleaseLabel();
		util.writeXmlConfiguration();
		util.checkMissingMessageProperties();
		if (util.getListMissingMessageProperties().size() > 0) {
			for (String name : util.getListMissingMessageProperties()) {
				System.out.println(name);
			}
			assertEquals("Wrong number of missing properties", 2, util.getListMissingMessageProperties().size());
		} else {
			assertTrue("None missing properties found!", false);
		}
	}

}
