package de.cimt.talendcomp.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.cimt.talendcomp.dev.ComponentUtil;
import de.cimt.talendcomp.dev.maven.CompDependency;

public class TestComponentUtil {
	
	@Test
	public void testReadAndWrite() throws Exception {
		ComponentUtil util = new ComponentUtil();
		CompDependency dep = new CompDependency();
		dep.setArtifactId("jlo-talendcomp-json");
		dep.setGroupId("de.jlo.talendcomp");
		dep.setVersion("13.2");
		dep.setSourceFile(new File("/Data/Talend/workspace_talend_comp/talendcomp_maven_plugins/test_libs/jlo-talendcomp-json-13.2.jar"));
		util.addBundledDependency(dep);
		util.setComponentBaseDir("/Data/Talend/workspace_talend_comp/talendcomp_maven_plugins/src/test/components/");
		util.setComponentName("tTest");
		util.setComponentVersion("1.0");
		util.readXmlConfiguration();
		util.clearComponentJars();
		util.copyJars();
		util.setupXMLImports(false, null, true);
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
