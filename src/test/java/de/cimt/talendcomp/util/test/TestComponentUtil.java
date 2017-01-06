package de.cimt.talendcomp.util.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cimt.talendcomp.dev.ComponentUtil;

public class TestComponentUtil {
	
	@Test
	public void testReadAndWrite() throws Exception {
		ComponentUtil util = new ComponentUtil();
		util.addJarFile("/Volumes/Data/Talend/workspace_talend_comp/talendcomp_ant_maven_plugins/src/test/resource/tTest/cimt-talendcomp-excel-8.0.jar");
		util.addJarFile("/Volumes/Data/Talend/workspace_talend_comp/talendcomp_ant_maven_plugins/src/test/resource/tTest/commons-codec-1.10.jar");
		util.addJarFile("/Volumes/Data/Talend/workspace_talend_comp/talendcomp_ant_maven_plugins/src/test/resource/tTest/commons-collections4-4.1.jar");
		util.addJarFile("/Volumes/Data/Talend/workspace_talend_comp/talendcomp_ant_maven_plugins/src/test/resource/tTest/curvesapi-1.04.jar");
		util.setComponentBaseDir("/Volumes/Data/Talend/workspace_talend_comp/talendcomp_ant_maven_plugins/talend_component");
		util.setComponentName("tTest");
		util.setComponentVersion("1.0");
		util.execute();
		assertTrue(true);
	}

}
