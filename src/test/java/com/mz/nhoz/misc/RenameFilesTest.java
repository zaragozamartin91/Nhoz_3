package com.mz.nhoz.misc;

import java.io.File;

import org.junit.Test;

public class RenameFilesTest {
	@Test
	public void testRenameFiles() {
		new File("C:\\Users\\martin\\Workspaces\\nhoz\\Nhoz_3\\testFiles\\LISTAPRE_1475116627940_TEMP.DBF")
				.renameTo(new File("C:\\Users\\martin\\Workspaces\\nhoz\\Nhoz_3\\testFiles\\LISTAPRE_1475116627940_RENOMBRADO.DBF"));
	}
}
