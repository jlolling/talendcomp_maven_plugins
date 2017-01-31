package de.cimt.talendcomp.util.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cimt.talendcomp.dev.ComponentUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import static org.junit.Assert.assertFalse;

public class TestComponentUtil {

    private final FilenameFilter jarFilter = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".jar");
        }
    };

    @Test
    public void testReadAndWrite() throws Exception {
        final String componentName = "tTest";
        final File resourceDir = new File("./src/test/resource/" + componentName + "/");
        File targetDir = new File("./target/test/" + componentName + "/");

        File xmlFile = new File(targetDir, componentName + "_java.xml");

        if (!xmlFile.exists()) {
            System.out.println("create targetDir " + targetDir);
            targetDir.mkdirs();

            if (new File(resourceDir, componentName + "_java.xml").exists()) {
                System.out.println("copy " + new File(resourceDir, componentName + "_java.xml") + " to " + resourceDir);

                Files.copy(
                        FileSystems.getDefault().getPath(new File(resourceDir, componentName + "_java.xml").getAbsolutePath()),
                        FileSystems.getDefault().getPath(targetDir.getAbsolutePath(), componentName + "_java.xml"),
                        StandardCopyOption.REPLACE_EXISTING);

            }
        }
        targetDir = new File("./target/test/");
        ComponentUtil util = new ComponentUtil();
        File[] jars = resourceDir.listFiles(jarFilter);
        for (File jar : jars) {
            util.addJarFile(jar.getCanonicalPath());
        }

        util.setComponentBaseDir(targetDir.getAbsolutePath());
        util.setComponentName("tTest");
        util.setComponentVersion("1.0");
        util.execute();

        if (targetDir.listFiles(jarFilter).length == jars.length) {
            assertFalse("Number of available jars differ.", false);
        } else {
            assertTrue(true);
        }
    }

}
