package grails.plugin.extproc

import java.io.File;
import java.util.List;
import grails.plugin.extproc.FileHandlingService;
import grails.test.*


class FileHandlingServiceTests extends GrailsUnitTestCase {
 	String nl
	def fileHandlingService
	 
	File tmpDir
	protected void setUp() {
        super.setUp()
		mockLogging(FileHandlingService, true)
		fileHandlingService = new FileHandlingService()
		nl = isWindows()?"\r\n":"\n"
    }

    protected void tearDown() {
        super.tearDown()
    }

	private boolean isWindows() {
		String nameOS = "os.name";
		return System.properties.get(nameOS).toString().toUpperCase().startsWith("WINDOWS")
	}
	
    void testCreateTempDirNoParam() {
		File tmp = fileHandlingService.createTempDir ()
		println tmp.absolutePath
		assert tmp.exists()
		assert tmp.isDirectory()
		
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
    }
	
	void testCreateTempDirUseExisting() {
		File tmp = fileHandlingService.createTempDir ("/tmp")
		println tmp.absolutePath
		assert tmp.exists()
		assert tmp.isDirectory()
		
	}
	
	void testCreateFileInTemp() {
		File tmp = fileHandlingService.createTempDir ()
		File myFile = fileHandlingService.fileInTemp (tmp, "name.txt")
		assert myFile.absolutePath.startsWith(tmp.absolutePath)
		
		myFile << "hallo\n"
		assert myFile.exists()
		assert !myFile.isDirectory()
		
		myFile.eachLine { l -> println l}
		
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
	}
	
	void testZipTempDirAndUnzip() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile = fileHandlingService.fileInTemp (tmp, "name.txt")
		500.times { myFile << "hallo" + nl }
		
		byte[] zip = fileHandlingService.zipDir (tmp, null)
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()
		fileHandlingService.unzipByteArrayToDir(zip, tmp2, null)
		//fileService.unzipFileToDir(zipped, tmp)
		zipped.delete()
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile = fileHandlingService.fileInTemp (tmp2, "name.txt")
		int cnt = 0
		myFile.eachLine { l -> 
			cnt++
			assert "hallo".equals (l)
		}
		assert cnt == 500
		fileHandlingService.delDirectory tmp2
		assert !tmp2.exists()		
	}
	
	
	void testZipTempDirToFileAndUnzipFromFile() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile = fileHandlingService.fileInTemp (tmp, "name.txt")
		4.times { myFile << "hallo" + nl }
		
		byte[] zip = fileHandlingService.zipDir (tmp, null)
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()
		fileHandlingService.unzipFileToDir(zipped, tmp2, null)
		zipped.delete()
		
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile = fileHandlingService.fileInTemp (tmp2, "name.txt")
		int cnt = 0
		myFile.eachLine { l ->
			cnt++
			assert "hallo".equals (l)
		}
		assert cnt == 4
		fileHandlingService.delDirectory tmp2
		assert !tmp2.exists()
	}
	
	void testZipTempIncludesDirAndUnzip() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile1 = fileHandlingService.fileInTemp (tmp, "name.txt")
		500.times { myFile1 << "hallo" + nl }
		File myFile2 = fileHandlingService.fileInTemp (tmp, "name2.txt")
		300.times { myFile2 << "hallo" + nl}

		
		byte[] zip = fileHandlingService.zipDir (tmp) { fn -> ["name2.txt"].contains(fn) }
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()
		fileHandlingService.unzipByteArrayToDir(zip, tmp2) {fn -> true}

		zipped.delete()
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile1 = fileHandlingService.fileInTemp (tmp2, "name.txt")
		assert !myFile1.exists() 
		myFile2 = fileHandlingService.fileInTemp (tmp2, "name2.txt")
		int cnt = 0
		myFile2.eachLine { l ->
			cnt++
			assert "hallo".equals (l)
		}
		assert cnt == 300
		fileHandlingService.delDirectory(tmp2)
		assert !tmp2.exists()
	}
	
	void testZipTempIncludesDirAndUnzipWithRegex() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile1 = fileHandlingService.fileInTemp (tmp, "name.txt")
		500.times { myFile1 << "hallo" + nl }
		File myFile2 = fileHandlingService.fileInTemp (tmp, "name2.txt")
		300.times { myFile2 << "hallo" + nl}

		
		byte[] zip = fileHandlingService.zipDir (tmp) { fn -> fn == "name2.txt" }
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()		
		fileHandlingService.unzipByteArrayToDir(zip, tmp2) { fn -> fn =~ /name2.txt$/}

		zipped.delete()
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile1 = fileHandlingService.fileInTemp (tmp2, "name.txt")
		assert !myFile1.exists()
		myFile2 = fileHandlingService.fileInTemp (tmp2, "name2.txt")
		int cnt = 0
		myFile2.eachLine { l ->
			cnt++
			assert "hallo".equals (l)
		}
		assert cnt == 300
		fileHandlingService.delDirectory(tmp2)
		assert !tmp2.exists()
	}
	
	
	
	void testZipTempIncludesDirAndUnzipWithRegex1Match() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile1 = fileHandlingService.fileInTemp (tmp, "name.txt")
		500.times { myFile1 << "hallo" + nl }
		File myFile2 = fileHandlingService.fileInTemp (tmp, "name2.txt")
		300.times { myFile2 << "hallo" + nl}

		
		byte[] zip = fileHandlingService.zipDir(tmp,null)
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()
		
		fileHandlingService.unzipByteArrayToDir(zip, tmp2)  { fn -> fn =~ /.*2.txt$/}

		zipped.delete()
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile1 = fileHandlingService.fileInTemp (tmp2, "name.txt")
		assert !myFile1.exists()
		myFile2 = fileHandlingService.fileInTemp (tmp2, "name2.txt")
		int cnt = 0
		myFile2.eachLine { l ->
			cnt++
			assert "hallo".equals (l)
		}
		assert cnt == 300
		fileHandlingService.delDirectory(tmp2)
		assert !tmp2.exists()
	}

	
	void testZipTempIncludesDirAndUnzipWithRegexNoMatch() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile1 = fileHandlingService.fileInTemp (tmp, "name.txt")
		5.times { myFile1 << "hallo" + nl }
		File myFile2 = fileHandlingService.fileInTemp (tmp, "name2.txt")
		3.times { myFile2 << "hallo" + nl}

		
		byte[] zip = fileHandlingService.zipDir (tmp, null)
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()
		
		fileHandlingService.unzipByteArrayToDir(zip, tmp2) { fn -> fn =~ / nomatch / }

		zipped.delete()
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile1 = fileHandlingService.fileInTemp (tmp2, "name.txt")
		assert !myFile1.exists()
		myFile2 = fileHandlingService.fileInTemp (tmp2, "name2.txt")
		assert !myFile2.exists()
		
		
		fileHandlingService.delDirectory(tmp2)
		assert !tmp2.exists()
	}

	
	void testZipTempIncludesDirAndUnzipWithClosure() {
		File tmp =  fileHandlingService.createTempDir ()
		File myFile1 = fileHandlingService.fileInTemp (tmp, "name.txt")
		5.times { myFile1 << "hallo" + nl }
		File myFile2 = fileHandlingService.fileInTemp (tmp, "name2.txt")
		3.times { myFile2 << "hallo" + nl}

		
		byte[] zip = fileHandlingService.zipDir (tmp) { fn -> ["name.txt","name2.txt"].contains(fn) }
		assert zip.size() > 0
		println "zip has ${zip.size()} bytes"
		fileHandlingService.delDirectory tmp
		assert !tmp.exists()
		
		File zipped = new File("/tmp/zip.zip")
		zipped << zip
		
		File tmp2 =  fileHandlingService.createTempDir ()
		
		fileHandlingService.unzipByteArrayToDir(zip, tmp2) { fn ->
			fn == "name2.txt"
		}

		zipped.delete()
		println "zip inflated, compressed size ${zip.size()} bytes"
		myFile1 = fileHandlingService.fileInTemp (tmp2, "name.txt")
		assert !myFile1.exists()
		myFile2 = fileHandlingService.fileInTemp (tmp2, "name2.txt")
		assert myFile2.exists()
		
		
		fileHandlingService.delDirectory(tmp2)
		assert !tmp2.exists()
	}


}
