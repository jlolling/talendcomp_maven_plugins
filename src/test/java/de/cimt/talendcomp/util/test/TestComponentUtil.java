package de.cimt.talendcomp.util.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cimt.talendcomp.dev.ComponentUtil;

public class TestComponentUtil {
	
	@Test
	public void testReadAndWrite() throws Exception {
		ComponentUtil util = new ComponentUtil();
		util.addJarFile("/Data/Talend/workspace_talend_comp/talendcomp_maven_plugins/test_libs/jlo-talendcomp-json-13.2.jar");
		util.setComponentBaseDir("/Data/Talend/workspace_talend_comp/talendcomp_maven_plugins/talend_component/");
		util.setComponentName("tTest");
		util.setComponentVersion("1.0");
		util.execute();
		assertTrue(true);
	}

}
