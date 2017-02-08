/**
 * Copyright 2015 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cimt.talendcomp.dev;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class ComponentUtil {
	
	private String componentName = null;
	private String componentBaseDir = null;
	private String componentSourceBaseDir = null;
	private String componentVersion = null;
	private String componentReleaseDate = null;
	private boolean addReleaseInfoAsLabel = true;
	private Document xmlDoc = null;
	private List<File> listJars = new ArrayList<File>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private File messagePropertiesFile = null;
	private Properties messages = new Properties();
	private List<String> listMissingMessageProperties = new ArrayList<String>();
	private static final String ignoreFilePatternStr = ".svn|.git|.DS_Store|.class";
	private Pattern ignoreFilePattern = null;
	
	public void addJarFile(String jarFilePath) throws Exception {
		File jar = new File(jarFilePath);
		if (jar.exists() == false) {
			throw new Exception("jar file: " + jarFilePath + " does not exist!");
		}
		listJars.add(jar);
	}

	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public String getComponentBaseDir() {
		return componentBaseDir;
	}
	
	public void setComponentBaseDir(String componentBaseDir) {
		this.componentBaseDir = componentBaseDir;
	}
	
	public String getComponentVersion() {
		return componentVersion;
	}
	
	public void setComponentVersion(String componentVersion) {
		this.componentVersion = componentVersion;
	}
	
	public String getComponentReleaseDate() {
		return componentReleaseDate;
	}
	
	public void setComponentReleaseDate(String componentReleaseDate) {
		this.componentReleaseDate = componentReleaseDate;
	}
	
	public boolean isAddReleaseInfoAsLabel() {
		return addReleaseInfoAsLabel;
	}
	
	public void setAddReleaseInfoAsLabel(boolean addReleaseInfoAsLabel) {
		this.addReleaseInfoAsLabel = addReleaseInfoAsLabel;
	}
	
	public void execute() throws Exception {
		readXmlConfiguration();
		clearComponentJars();
		copyJars();
		setupXMLImports();
		writeXmlConfiguration();
	}
	
	public String readXmlConfiguration() throws Exception {
		if (componentBaseDir == null) {
			throw new IllegalStateException("componentBaseDir not set!");
		}
		if (componentName == null) {
			throw new IllegalStateException("componentName not set!");
		}
		File dir = new File(componentBaseDir, componentName);
		File xmlFile = new File(dir, componentName + "_java.xml");
		if (xmlFile.exists() == false) {
			throw new Exception("XML configuration file: " + xmlFile.getAbsolutePath() + " does not exist!");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		xmlDoc = DocumentHelper.parseText(sb.toString());
		return xmlFile.getAbsolutePath();
	}
	
	public String writeXmlConfiguration() throws Exception {
		if (componentBaseDir == null) {
			throw new IllegalStateException("componentBaseDir not set!");
		}
		if (componentName == null) {
			throw new IllegalStateException("componentName not set!");
		}
		File dir = new File(componentBaseDir, componentName);
		File xmlFile = new File(dir, componentName + "_java.xml");
		if (xmlFile.exists() == false) {
			throw new Exception("XML configuration file: " + xmlFile.getAbsolutePath() + " does not exist, but is mandatory for a Talend component!");
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile), format );
        writer.write( xmlDoc );
        writer.close();
		return xmlFile.getAbsolutePath();
	}
	
	public int clearComponentJars() {
		if (componentBaseDir == null) {
			throw new IllegalStateException("componentBaseDir not set!");
		}
		if (componentName == null) {
			throw new IllegalStateException("componentName not set!");
		}
		File dir = new File(componentBaseDir, componentName);
		File[] files = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name != null && name.toLowerCase().endsWith(".jar");
			}
			
		});
		int count = 0;
		for (File jarFile : files) {
			if (jarFile.delete()) {
				count++;
			}
		}
		return count;
	}
	
	public int copyJars() throws Exception {
		if (componentBaseDir == null) {
			throw new IllegalStateException("componentBaseDir not set!");
		}
		if (componentName == null) {
			throw new IllegalStateException("componentName not set!");
		}
		int count = 0;
		File dir = new File(componentBaseDir, componentName);
		for (File jarFile : listJars) {
			if (jarFile.exists() == false) {
				throw new Exception("");
			}
			File targetFile = new File(dir.getAbsolutePath(), jarFile.getName());
			copyFile(jarFile, targetFile);
			count++;
		}
		return count;
	}
	
	private String getJarCommonName(String fileName) {
		int pos = fileName.lastIndexOf("-");
		if (pos > 0) {
			if (fileName.contains("SNAPSHOT")) {
				int pos2 = fileName.lastIndexOf("-", pos - 1);
				return fileName.substring(0, pos2);
			} else {
				return fileName.substring(0, pos);
			}
		} else {
			return fileName;
		}
	}
	
	private String getReleaseDate() {
		if (componentReleaseDate == null) {
			componentReleaseDate = sdf.format(new Date());
		}
		return componentReleaseDate;
	}
	
	public void setupXMLImports() throws Exception {
		Element importsNode = (Element) xmlDoc.selectSingleNode( "/COMPONENT/CODEGENERATION/IMPORTS" );
		if (importsNode == null) {
			// we must create an IMPORTS node
			Element codeGenNode = (Element) xmlDoc.selectSingleNode( "/COMPONENT/CODEGENERATION" );
			if (codeGenNode == null) {
				Element compNode = (Element) xmlDoc.selectSingleNode( "/COMPONENT" );
				if (compNode == null) {
					throw new IllegalStateException("There is no COMPONENT tag. This is not a valid Talend component descriptor document!");
				} else {
					codeGenNode = compNode.addElement("CODEGENERATION");
					importsNode = codeGenNode.addElement("IMPORTS");
				}
			} else {
				importsNode = codeGenNode.addElement("IMPORTS");
			}
		}
		// remove existing IMPORT tags
		@SuppressWarnings("unchecked")
		List<Node> importNodes = importsNode.selectNodes("/COMPONENT/CODEGENERATION/IMPORTS/IMPORT");
		for (Node n : importNodes) {
			n.detach();
		}
		// add new jars as IMPORT tags
		for (File jar : listJars) {
			importsNode.addElement("IMPORT")
				.addAttribute("NAME", getJarCommonName(jar.getName()))
				.addAttribute("MODULE", jar.getName())
				.addAttribute("REQUIRED", "true");
		}
	}
	
	public void setupXMLReleaseLabel() {
		Element headerNode = (Element) xmlDoc.selectSingleNode( "/COMPONENT/HEADER" );
		headerNode.addAttribute("RELEASE_DATE", getReleaseDate());
		if (componentVersion != null && componentVersion.trim().isEmpty() == false) {
			headerNode.addAttribute("VERSION", componentVersion);
		}
		Element advancedParams = (Element) xmlDoc.selectSingleNode( "/COMPONENT/ADVANCED_PARAMETERS" );
		if (advancedParams == null) {
			throw new IllegalStateException("There is no ADVANCED_PARAMETERS tag. This is not a valid Talend component descriptor document!");
		} else {
			// remove old node
			@SuppressWarnings("unchecked")
			List<Element> releaseNodes = xmlDoc.selectNodes("/COMPONENT/ADVANCED_PARAMETERS/PARAMETER");
			for (Element e : releaseNodes) {
				Attribute attr = e.attribute("NAME");
				if (attr != null) {
					String name = attr.getText();
					if (name != null && name.startsWith("RELEASE_LABEL")) {
						e.detach();
					}
				}
			}
			Element releaseElement = advancedParams.addElement("PARAMETER")
				.addAttribute("NAME", "RELEASE_LABEL_" + getReleaseDate())
				.addAttribute("FIELD", "LABEL")
				.addAttribute("COLOR", "0;0;0")
				.addAttribute("NUM_ROW", "900");
			releaseElement.addElement("DEFAULT").addText("Release: " + componentVersion + " build at: " + getReleaseDate());
		}
	}

	private String readDefaultMessageProperties() throws Exception {
		if (componentBaseDir == null) {
			throw new IllegalStateException("componentBaseDir not set!");
		}
		if (componentName == null) {
			throw new IllegalStateException("componentName not set!");
		}
		File dir = new File(componentBaseDir, componentName);
		messagePropertiesFile = new File(dir, componentName + "_messages.properties");
		if (messagePropertiesFile.exists() == false) {
			throw new Exception("Message properties file: " + messagePropertiesFile.getAbsolutePath() + " does not exist, but is mandatory for a Talend component!");
		}
		FileInputStream in = new FileInputStream(messagePropertiesFile);
		messages.load(in);
		in.close();
		return messagePropertiesFile.getAbsolutePath();
	}
	
	@SuppressWarnings("unchecked")
	public String checkMissingMessageProperties() throws Exception {
		String fileName = readDefaultMessageProperties();
		listMissingMessageProperties.clear();
		// read simple parameter names
		List<Node> paramNameNodes = xmlDoc.selectNodes("/COMPONENT/*/PARAMETER[@FIELD!='LABEL']/@NAME");
		for (Node node : paramNameNodes) {
			String name = node.getStringValue().trim();
			if ("PROPERTY".equals(name) == false) {
				String key = name + ".NAME";
				if (messages.containsKey(key) == false) {
					listMissingMessageProperties.add(key);
				}
			}
		}
		// read closed list parameters
		paramNameNodes = xmlDoc.selectNodes("/COMPONENT/*/PARAMETER[@FIELD ='CLOSED_LIST' or @FIELD='TABLE']/@NAME");
		for (Node node : paramNameNodes) {
			String name = node.getStringValue().trim();
			List<Node> itemNodes1 = xmlDoc.selectNodes("/COMPONENT/*/PARAMETER[@NAME='" + name + "']/ITEMS/ITEM/@NAME");
			for (Node itemNode1 : itemNodes1) {
				String itemNodeName1 = itemNode1.getStringValue().trim();
				String key1 = name + ".ITEM." + itemNodeName1;
				if (messages.containsKey(key1) == false) {
					listMissingMessageProperties.add(key1);
				}
				// check list in items
				List<Node> itemNodes2 = xmlDoc.selectNodes("/COMPONENT/*/PARAMETER[@NAME='" + name + "']/ITEMS/ITEM[@NAME='" + itemNodeName1 + "']/ITEMS/ITEM/@NAME");
				for (Node itemNode2 : itemNodes2) {
					String itemNodeName2 = itemNode2.getStringValue().trim();
					String key2 = key1 + ".ITEM." + itemNodeName2;
					if (messages.containsKey(key2) == false) {
						listMissingMessageProperties.add(key2);
					}
				}
			}
		}
		// read return value names
		paramNameNodes = xmlDoc.selectNodes("/COMPONENT/RETURNS/RETURN/@NAME");
		for (Node node : paramNameNodes) {
			String name = node.getStringValue().trim();
			String key = name + ".NAME";
			if (messages.containsKey(key) == false) {
				listMissingMessageProperties.add(key);
			}
		}
		// read connector names
		paramNameNodes = xmlDoc.selectNodes("/COMPONENT/CONNECTORS/CONNECTOR/@NAME");
		for (Node node : paramNameNodes) {
			String name = node.getStringValue().trim();
			String keyMenu = name + ".MENU";
			if (messages.containsKey(keyMenu) == false) {
				listMissingMessageProperties.add(keyMenu);
			}
			String keyLink = name + ".LINK";
			if (messages.containsKey(keyLink) == false) {
				listMissingMessageProperties.add(keyLink);
			}
		}
		String longNameKey = "LONG_NAME.NAME";
		if (messages.contains(longNameKey) == false) {
			listMissingMessageProperties.add(longNameKey);
		}
		return fileName;
	}

	public List<String> getListMissingMessageProperties() {
		return listMissingMessageProperties;
	}

	public String getComponentSourceBaseDir() {
		return componentSourceBaseDir;
	}

	public void setComponentSourceBaseDir(String componentSourceBaseDir) {
		this.componentSourceBaseDir = componentSourceBaseDir;
	}
	
	public int copyResources() throws Exception {
		if (componentName == null) {
			throw new Exception("componentName not set!");
		}
		if (componentSourceBaseDir == null || componentSourceBaseDir.trim().isEmpty()) {
			throw new Exception("copyResources failed: componentSourceBaseDir is not set!");
		}
		File sourceDir = new File(componentSourceBaseDir, componentName);
		if (sourceDir.exists() == false) {
			throw new Exception("copyResources failed: sourceDir: " + sourceDir.getAbsolutePath() + " does not exists or is not readable!");
		}
		if (componentBaseDir == null || componentBaseDir.trim().isEmpty()) {
			throw new Exception("copyResources failed: componentBaseDir is not set!");
		}
		File targetDir = new File(componentBaseDir, componentName);
		if (targetDir.exists() == false) {
			targetDir.mkdirs();
		} else {
			cleanTarget(targetDir);
		}
		if (targetDir.exists() == false) {
			throw new Exception("copyResources failed: componentBaseDir: " + targetDir.getAbsolutePath() + " does not exist and cannot be created!");
		}
		// select resources
		File[] sourceFiles = sourceDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				Matcher m = getIgnoreFilePattern().matcher(pathname.getAbsolutePath());
				if (m.find()) {
					return false;
				} else {
					return true;
				}
			}
		});
		// copy resources
		int count = 0;
		for (File source : sourceFiles) {
			File target = new File(targetDir, source.getName());
			copyFile(source, target);
			count++;
		}
		return count;
	}
	
	private void cleanTarget(File targetDir) {
		if (targetDir.exists()) {
			File[] files = targetDir.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
	}
	
	private void copyFile(File source, File target) throws Exception {
		if (source.exists() == false || source.canRead() == false) {
			throw new Exception("Copy file: " + source.getAbsolutePath() + " failed: file doe not exist.");
		}
		if (target.equals(source)) {
			throw new Exception("Copy source file: " + source.getAbsolutePath() + " to target file: " + target.getAbsolutePath() + " failed: Source and target are the same.");
		}
		BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(target));
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(source));
		byte[] buffer = new byte[1024];
		int len = 0;
		Exception ex = null;
		try {
			while ((len = bi.read(buffer)) != -1) {
				bo.write(buffer, 0, len);
			}
		} catch (Exception e) {
			ex = e;
		} finally {
			if (bo != null) {
				bo.flush();
				bo.close();
			}
			if (bi != null) {
				bi.close();
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	private Pattern getIgnoreFilePattern() {
		if (ignoreFilePattern == null) {
			ignoreFilePattern = Pattern.compile(ignoreFilePatternStr);
		}
		return ignoreFilePattern;
	}
	
}
