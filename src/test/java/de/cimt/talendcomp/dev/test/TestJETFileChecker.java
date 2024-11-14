package de.cimt.talendcomp.dev.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import de.cimt.talendcomp.dev.JETFileChecker;

public class TestJETFileChecker {

	@Test
	public void testNoTags() throws IOException {
		System.out.println("### testNoTags");
		String test = "this is a test content\nwhithout tags";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		if (result != null) {
			assertTrue("Found invalid error message: " + result, false);
		}
	}

	@Test
	public void testOpenCloseCorrect1() throws IOException {
		System.out.println("### testOpenCloseCorrect1");
		String test = "this is a <% test %> content\n <%=without%> tags";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		System.out.println(result);
		if (result != null) {
			assertTrue("Found invalid error message: " + result, false);
		}
	}

	@Test
	public void testOpenCloseCorrect2() throws IOException {
		System.out.println("### testOpenCloseCorrect2");
		String test = "this is a <% test content\n without%> tags";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		System.out.println(result);
		if (result != null) {
			assertTrue("Found invalid error message: " + result, false);
		} else {
			assertEquals("Wrong number of tags", 2, c.getCountTags());
		}
	}

	@Test
	public void testOpenCloseFail1() throws IOException {
		System.out.println("### testOpenCloseFail1");
		String test = "this is a <% test content\n without tags";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		System.out.println(result);
		if (result == null) {
			assertTrue("Found no message", false);
		} else {
			assertEquals("Wrong number of tags", 1, c.getCountTags());
		}
		assertTrue("Wrong line number found", result.startsWith("File content has an (last) open tags at line: 1"));
	}

	@Test
	public void testOpenCloseFail2() throws IOException {
		System.out.println("### testOpenCloseFail2");
		String test = "this is a <% test \n<% content\n without%> tags";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		System.out.println(result);
		if (result == null) {
			assertTrue("Found no message", false);
		} else {
			assertEquals("Wrong number of tags", 2, c.getCountTags());
		}
		assertTrue("Wrong line number found", result.startsWith("Line: 2"));
	}

	@Test
	public void testOpenCloseFail3() throws IOException {
		System.out.println("### testOpenCloseFail3");
		String test = "this is a <% test content\n\n%>\n tags %> ";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		System.out.println(result);
		if (result == null) {
			assertTrue("Found no message", false);
		} else {
			assertEquals("Wrong number of tags", 3, c.getCountTags());
		}
		assertTrue("Wrong line number found", result.startsWith("Line: 4"));
		assertTrue("Wrong column found", result.contains("column: 7"));
	}

	@Test
	public void testOpenCloseFail4() throws IOException {
		System.out.println("### testOpenCloseFail4");
		String test = "this is a <% test content\n without%> tags %>";
		JETFileChecker c = new JETFileChecker();
		String result = c.checkContent(test);
		System.out.println(result);
		if (result == null) {
			assertTrue("Found no message", false);
		} else {
			assertEquals("Wrong number of tags", 3, c.getCountTags());
		}
		assertTrue("Wrong line number found", result.startsWith("Line: 2"));
		assertTrue("Wrong column found", result.contains("column: 17"));
	}

}
