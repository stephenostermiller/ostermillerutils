/*
 * Regression tests for FileHelper
 * Copyright (C) 2026 Stephen Ostermiller
 * https://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See LICENSE.txt for details.
 */
package com.Ostermiller.util;

import junit.framework.TestCase;
import java.io.*;

/**
 * Unit test for FileHelper.
 * More information about this class is available from <a target="_top" href=
 * "https://ostermiller.org/utils/FileHelper.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class FileHelperTest extends TestCase {

	private File tempDir;
	private File sourceFile;
	private File destFile;

	protected void setUp() throws Exception {
		super.setUp();
		tempDir = new File(System.getProperty("java.io.tmpdir"), "FileHelperTest_" + System.nanoTime());
		tempDir.mkdir();
		sourceFile = new File(tempDir, "source.txt");
		destFile = new File(tempDir, "dest.txt");
	}

	protected void tearDown() throws Exception {
		if (sourceFile != null && sourceFile.exists()) {
			sourceFile.delete();
		}
		if (destFile != null && destFile.exists()) {
			destFile.delete();
		}
		if (tempDir != null && tempDir.exists()) {
			tempDir.delete();
		}
		super.tearDown();
	}

	public void testMoveSimpleFile() throws IOException {
		// Create source file with content
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("test content");
		fw.close();

		assertTrue(sourceFile.exists());
		assertFalse(destFile.exists());

		// Move the file
		FileHelper.move(sourceFile, destFile);

		// Verify source is gone and destination exists with content
		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());

		// Verify content
		BufferedReader br = new BufferedReader(new FileReader(destFile));
		String content = br.readLine();
		br.close();
		assertEquals("test content", content);
	}

	public void testMoveToExistingFileThrows() throws IOException {
		// Create source file
		FileWriter fw1 = new FileWriter(sourceFile);
		fw1.write("source");
		fw1.close();

		// Create destination file
		FileWriter fw2 = new FileWriter(destFile);
		fw2.write("destination");
		fw2.close();

		assertTrue(sourceFile.exists());
		assertTrue(destFile.exists());

		// Try to move without overwrite - should throw
		try {
			FileHelper.move(sourceFile, destFile, false);
			fail("Expected IOException");
		} catch (IOException e) {
			// Expected
		}

		// Files should still exist
		assertTrue(sourceFile.exists());
		assertTrue(destFile.exists());
	}

	public void testMoveToExistingFileWithOverwrite() throws IOException {
		// Create source file
		FileWriter fw1 = new FileWriter(sourceFile);
		fw1.write("new content");
		fw1.close();

		// Create destination file
		FileWriter fw2 = new FileWriter(destFile);
		fw2.write("old content");
		fw2.close();

		assertTrue(sourceFile.exists());
		assertTrue(destFile.exists());

		// Move with overwrite
		FileHelper.move(sourceFile, destFile, true);

		// Source should be gone, destination should have new content
		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());

		BufferedReader br = new BufferedReader(new FileReader(destFile));
		String content = br.readLine();
		br.close();
		assertEquals("new content", content);
	}

	public void testMoveEmptyFile() throws IOException {
		// Create empty source file
		sourceFile.createNewFile();
		assertTrue(sourceFile.exists());
		assertEquals(0, sourceFile.length());

		// Move empty file
		FileHelper.move(sourceFile, destFile);

		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());
		assertEquals(0, destFile.length());
	}

	public void testMoveLargeFile() throws IOException {
		// Create source file with large content
		FileWriter fw = new FileWriter(sourceFile);
		for (int i = 0; i < 10000; i++) {
			fw.write("This is line " + i + "\n");
		}
		fw.close();

		long sourceSize = sourceFile.length();
		assertTrue(sourceSize > 0);

		// Move the file
		FileHelper.move(sourceFile, destFile);

		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());
		assertEquals(sourceSize, destFile.length());
	}

	public void testMoveFileWithBinaryContent() throws IOException {
		// Create file with binary content
		FileOutputStream fos = new FileOutputStream(sourceFile);
		byte[] data = new byte[256];
		for (int i = 0; i < 256; i++) {
			data[i] = (byte) i;
		}
		fos.write(data);
		fos.close();

		long sourceSize = sourceFile.length();

		// Move the file
		FileHelper.move(sourceFile, destFile);

		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());
		assertEquals(sourceSize, destFile.length());

		// Verify binary content
		FileInputStream fis = new FileInputStream(destFile);
		byte[] readData = new byte[(int) destFile.length()];
		fis.read(readData);
		fis.close();

		for (int i = 0; i < 256; i++) {
			assertEquals((byte) i, readData[i]);
		}
	}

	public void testMoveMultipleTimesChained() throws IOException {
		// Create source
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("test");
		fw.close();

		File intermediate = new File(tempDir, "intermediate.txt");
		File finalDest = new File(tempDir, "final.txt");

		try {
			// Move to intermediate
			FileHelper.move(sourceFile, intermediate);
			assertFalse(sourceFile.exists());
			assertTrue(intermediate.exists());

			// Move to final
			FileHelper.move(intermediate, finalDest);
			assertFalse(intermediate.exists());
			assertTrue(finalDest.exists());

			// Verify content
			BufferedReader br = new BufferedReader(new FileReader(finalDest));
			String content = br.readLine();
			br.close();
			assertEquals("test", content);
		} finally {
			if (intermediate.exists()) intermediate.delete();
			if (finalDest.exists()) finalDest.delete();
		}
	}

	public void testMovePreservesFileSize() throws IOException {
		// Create source with known content
		FileWriter fw = new FileWriter(sourceFile);
		String testContent = "The quick brown fox jumps over the lazy dog";
		fw.write(testContent);
		fw.close();

		long originalSize = sourceFile.length();

		// Move the file
		FileHelper.move(sourceFile, destFile);

		assertEquals(originalSize, destFile.length());
	}

	public void testMovePreservesContent() throws IOException {
		// Create source with specific content
		FileWriter fw = new FileWriter(sourceFile);
		String[] lines = {
			"Line 1",
			"Line 2 with special chars: !@#$%^&*()",
			"Line 3 with numbers: 0123456789"
		};
		for (String line : lines) {
			fw.write(line + "\n");
		}
		fw.close();

		// Move the file
		FileHelper.move(sourceFile, destFile);

		// Verify content line by line
		BufferedReader br = new BufferedReader(new FileReader(destFile));
		for (String expectedLine : lines) {
			String readLine = br.readLine();
			assertEquals(expectedLine, readLine);
		}
		br.close();
	}

	public void testMoveFileToSubdirectory() throws IOException {
		// Create source
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("test");
		fw.close();

		// Create subdirectory
		File subdir = new File(tempDir, "subdir");
		subdir.mkdir();
		File destInSubdir = new File(subdir, "moved.txt");

		try {
			// Move to subdirectory
			FileHelper.move(sourceFile, destInSubdir);

			assertFalse(sourceFile.exists());
			assertTrue(destInSubdir.exists());
		} finally {
			if (destInSubdir.exists()) destInSubdir.delete();
			if (subdir.exists()) subdir.delete();
		}
	}

	public void testMoveWithoutOverwritePreservesOriginal() throws IOException {
		// Create source
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("source content");
		fw.close();

		// Create destination
		FileWriter fw2 = new FileWriter(destFile);
		fw2.write("dest content");
		fw2.close();

		// Try to move without overwrite
		try {
			FileHelper.move(sourceFile, destFile, false);
			fail("Should throw IOException");
		} catch (IOException e) {
			// Expected
		}

		// Source should still exist with original content
		assertTrue(sourceFile.exists());
		BufferedReader br = new BufferedReader(new FileReader(sourceFile));
		assertEquals("source content", br.readLine());
		br.close();
	}

	public void testMoveFileNameChange() throws IOException {
		// Create source with specific name
		File source = new File(tempDir, "original_name.txt");
		FileWriter fw = new FileWriter(source);
		fw.write("content");
		fw.close();

		File dest = new File(tempDir, "new_name.txt");

		try {
			FileHelper.move(source, dest);
			assertFalse(source.exists());
			assertTrue(dest.exists());
			assertEquals("new_name.txt", dest.getName());
		} finally {
			if (dest.exists()) dest.delete();
		}
	}

	public void testMoveUnicodFilename() throws IOException {
		File source = new File(tempDir, "test_日本語.txt");
		FileWriter fw = new FileWriter(source);
		fw.write("content");
		fw.close();

		File dest = new File(tempDir, "dest_日本語.txt");

		try {
			FileHelper.move(source, dest);
			assertFalse(source.exists());
			assertTrue(dest.exists());
		} finally {
			if (dest.exists()) dest.delete();
		}
	}

	public void testMoveFileWithUTF8Content() throws IOException {
		FileOutputStream fos = new FileOutputStream(sourceFile);
		String utf8Content = "Hello 世界 Привет مرحبا";
		fos.write(utf8Content.getBytes("UTF-8"));
		fos.close();

		FileHelper.move(sourceFile, destFile);

		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());

		// Verify UTF-8 content
		BufferedReader br = new BufferedReader(
			new InputStreamReader(new FileInputStream(destFile), "UTF-8")
		);
		String readContent = br.readLine();
		br.close();
		assertEquals(utf8Content, readContent);
	}

	public void testMoveRetainsFileTimestamp() throws IOException, InterruptedException {
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("content");
		fw.close();

		long originalTime = sourceFile.lastModified();
		assertTrue("Source file should have a valid timestamp", originalTime > 0);

		Thread.sleep(100); // Ensure time difference if we check later

		FileHelper.move(sourceFile, destFile);

		// File should have moved and destination should have a timestamp
		assertFalse(sourceFile.exists());
		assertTrue(destFile.exists());
		assertTrue("Destination should have a valid timestamp", destFile.lastModified() > 0);
	}

	public void testMoveFileWithSpacesInName() throws IOException {
		File source = new File(tempDir, "file with spaces.txt");
		FileWriter fw = new FileWriter(source);
		fw.write("content");
		fw.close();

		File dest = new File(tempDir, "new file with spaces.txt");

		try {
			FileHelper.move(source, dest);
			assertFalse(source.exists());
			assertTrue(dest.exists());
		} finally {
			if (dest.exists()) dest.delete();
		}
	}

	public void testMoveReadOnlyFile() throws IOException {
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("content");
		fw.close();

		// Make file read-only (may not work on all systems)
		sourceFile.setReadOnly();

		try {
			FileHelper.move(sourceFile, destFile);
			assertTrue(destFile.exists());
		} finally {
			if (destFile.exists()) destFile.delete();
		}
	}

	public void testMoveToSameName() throws IOException {
		// Create source
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("content");
		fw.close();

		File sameName = new File(tempDir, "source.txt");

		// Moving to same name should throw IOException (destination exists)
		try {
			FileHelper.move(sourceFile, sameName);
			fail("Expected IOException when moving to same name");
		} catch (IOException e) {
			// Expected - destination already exists
			assertTrue(sourceFile.exists());
		}
	}

	public void testMovePreservesExecutableBit() throws IOException {
		FileWriter fw = new FileWriter(sourceFile);
		fw.write("#!/bin/bash\necho hello");
		fw.close();

		// Make source executable
		sourceFile.setExecutable(true);
		assertTrue(sourceFile.canExecute());

		FileHelper.move(sourceFile, destFile);

		assertTrue(destFile.exists());
		assertFalse(sourceFile.exists());
		assertTrue("Destination should be executable", destFile.canExecute());
	}
}
