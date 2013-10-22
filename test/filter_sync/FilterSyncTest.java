package filter_sync;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FilterSyncTest {
	@Test
	public void testFilter() throws IOException {
		try (ZipInputStream in = new ZipInputStream(new FileInputStream("sample/simple/doc.zip"))) {
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				System.out.println(entry.getName());
			}
		}

		ZipFile source = new ZipFile("sample/simple/doc.zip");
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("sample/simple/out/doc.zip"));
		ZipEntry entry = source.getEntry("");
		IOUtils.copy(source.getInputStream(entry), zos);

		FileUtils.deleteDirectory();
	}
}
