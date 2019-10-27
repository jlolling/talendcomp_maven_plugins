package de.cimt.talendcomp.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cimt.talendcomp.dev.ComponentUtil;

public class TestComponentUtil {
	
	@Test
	public void testReadAndWrite() throws Exception {
		ComponentUtil util = new ComponentUtil();
		util.addJarFile("/Data/Talend/workspace_talend_comp/talendcomp_maven_plugins/test_libs/jlo-talendcomp-json-13.2.jar");
		util.setComponentBaseDir("/Data/Talend/workspace_talend_comp/talendcomp_maven_plugins/src/test/components/");
		util.setComponentName("tTest");
		util.setComponentVersion("1.0");
		util.readXmlConfiguration();
		util.clearComponentJars();
		util.copyJars();
		util.setupXMLImports(false, null);
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
