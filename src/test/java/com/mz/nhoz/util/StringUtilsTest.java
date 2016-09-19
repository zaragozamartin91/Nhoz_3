package com.mz.nhoz.util;

import static org.junit.Assert.*;
import static com.mz.nhoz.util.StringUtils.*;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testRemoveLeadingZeroes() {
		assertEquals("ABCD", removeLeadingZeroes("000ABCD"));
		assertEquals("ABCD", removeLeadingZeroes("ABCD"));
		assertEquals("1234", removeLeadingZeroes("01234"));
	}

}
