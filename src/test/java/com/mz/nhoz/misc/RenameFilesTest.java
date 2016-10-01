package com.mz.nhoz.misc;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class RenameFilesTest {
	@Test
	public void testRenameFiles() {
		assertTrue(new File("C:\\Users\\martin\\Workspaces\\nhoz\\Nhoz_3\\testFiles\\LISTAPRE_2016101_TEMP.DBF")
				.renameTo(new File("C:\\Users\\martin\\Workspaces\\nhoz\\Nhoz_3\\testFiles\\LISTAPRE_RENOMBRADO.DBF")));
	}
}
