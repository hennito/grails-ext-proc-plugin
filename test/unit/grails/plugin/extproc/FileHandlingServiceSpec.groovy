package grails.plugin.extproc

import grails.test.GrailsUnitTestCase


import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestFor
import grails.test.mixin.Mock

import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(FileHandlingService)
class FileHandlingServiceSpec extends Specification { 	
 	static final String nl = isWindows() ? "\r\n" : "\n" 	
	static final String HELLO = "hello world"
	static final String HELLO_LINE = "hello world" + nl

	@Shared File tmp

	def setup() {    
		tmp = service.createTempDir()	
    }

    def cleanup() {
    	service.delDirectory tmp
    }

	private static boolean isWindows() {
		String nameOS = "os.name"
		return System.properties.get(nameOS).toString().toUpperCase().startsWith("WINDOWS")
	}

	void "test createTempDir no parameter"() {
		when:
			File tmpDir = service.createTempDir ()
		then:
			tmpDir.exists()
			tmpDir.isDirectory()
		
		when:
			service.delDirectory tmpDir
		then:
			!tmpDir.exists()
	}

	void "test createTempDir under system temp"() {
		when:
			String tempDirStr = tmp.absolutePath + "/myTmpTestDirFileHandlingService"
			File existingDir = new File(tempDirStr)
			existingDir.delete()
			existingDir.mkdir()
		then: 
			existingDir.exists()
			existingDir.isDirectory()
			existingDir.absolutePath.startsWith(tempDirStr)

		when:
			File tmpDir = service.createTempDir(tempDirStr)
		then:
			tmpDir.exists()
			tmpDir.isDirectory()
			tmpDir.absolutePath.startsWith(tempDirStr)
			existingDir.absolutePath == tmpDir.absolutePath

		when:
			service.delDirectory tmpDir
		then:
			!tmpDir.exists()
	}


	void "test fileInTemp"() {
		when:
			File myFile = service.fileInTemp (tmp, "name.txt")
		then:
			myFile.absolutePath.startsWith(tmp.absolutePath)

		when:
			myFile << HELLO_LINE
		then:
			myFile.exists()
			!myFile.isDirectory()
	}

	void "test zipDir to byte array  And Unzip to new temp file"() {
		given: "a file of known content"
			File myFile = createHelloFileInTmp("name.txt", 500)

		when: "we zip the directory content (=the file) into a bytearray"
			byte[] zip = service.zipDir (tmp, null)
		and: "delete the file"
			myFile.delete()			

		then: "the file is gone but we got it zipped"
			zip.length > 0		
			!myFile.exists()

		when: "we then unzip the bytearray to a new tempdir "
			File tmp2 =  service.createTempDir ()
			service.unzipByteArrayToDir(zip, tmp2, null)
		and: "read the unzipped file of known content"
			myFile = service.fileInTemp (tmp2, "name.txt")
		then: "the content matches the known content"
			500 == countHelloLines(myFile)

		cleanup:
			service.delDirectory tmp2
	}

	void "test zipTempDir to File and Unzip from File"() {
		given: 
			String filename = "hello.txt"
			createHelloFileInTmp(filename, 4)

		when:
			byte[] zip = service.zipDir (tmp, null)
			service.delDirectory tmp
		then: 
			!tmp.exists()

		when:
			tmp =  service.createTempDir()
			File zipped = service.fileInTemp (tmp, "zip.zip")
			zipped << zip

		and:
			File tmp2 =  service.createTempDir ()
			service.unzipFileToDir(zipped, tmp2, null)
			zipped.delete()
		then:		
			4 == countHelloLines(service.fileInTemp(tmp2, filename))

		cleanup:			
			service.delDirectory tmp2

	}

	void "test regex closure for zip and unzip"() {
		given: "2 files with known content"
			File myFile1 = createHelloFileInTmp("name.txt", 500)
			File myFile2 = createHelloFileInTmp("name2.txt", 300)

		when: "we zip those files (null as closure includes the file) and delete them"
			byte[] zip = service.zipDir(tmp, null)
			myFile1.delete()
			myFile2.delete()
		then: "the zip has content and the files are gone"
			zip.size() > 0
			!myFile1.exists()
			!myFile2.exists()

		when: "we unzip the file ending in 2.txt from the zip to a new tmp folder"
			File tmp2 =  service.createTempDir ()
			service.unzipByteArrayToDir(zip, tmp2)  { fn -> fn =~ /.*2.txt$/}
		and: "we try to reconstruct the files"
			myFile1 = service.fileInTemp (tmp2, "name.txt")
			myFile2 = service.fileInTemp (tmp2, "name2.txt")

		then: "only the file ending in 2.txt exists and matches the known content"
			!myFile1.exists()
			300 == countHelloLines(myFile2)

		cleanup:
			service.delDirectory tmp2

	}

	void "test zip is empty when regex/closure mismatch"() {
		given: "2 files with known content"
			File myFile1 = createHelloFileInTmp("name.txt", 5)
			File myFile2 = createHelloFileInTmp("name2.txt", 3)

		when:
			byte[] zip = service.zipDir (tmp, null)		
		then:
			zip.size() > 0
			

		when: 
			File tempDirectory = service.createTempDir()
		and:
			service.unzipByteArrayToDir(zip, tempDirectory) { fn -> fn =~ / nomatch / }
		then:
			tempDirectory.list().collect().isEmpty()

		cleanup:
			service.delDirectory tempDirectory
	}

	void "test closure works on zipDir and unzipDir"() {
		given: "2 files with known content"
			File myFile1 = createHelloFileInTmp("name.txt", 5)
			File myFile2 = createHelloFileInTmp("name2.txt", 3)

		when: "we zip them both"
			byte[] zip = service.zipDir (tmp) { fn -> ["name.txt","name2.txt"].contains(fn) }
		then:
			zip.size() > 0

		when:
			myFile1.delete()
			myFile2.delete()
		then:
			!myFile1.exists()
			!myFile2.exists()

		when: "we unzip just one of them"
			File tmpD =  service.createTempDir()			
			service.unzipByteArrayToDir(zip, tmpD) { fn -> fn == "name2.txt" }
		and:
			myFile1 = service.fileInTemp (tmpD, "name.txt")
			myFile2 = service.fileInTemp (tmpD, "name2.txt")

		then: "only one exists"
			!myFile1.exists()
			myFile2.exists()

		cleanup:
			service.delDirectory tmpD
	}



	private File createHelloFileInTmp(String filename, int lines) {
		File myFile = service.fileInTemp (tmp, filename)
		lines.times { myFile << HELLO_LINE }
		return myFile
	}

	private int countHelloLines(File input) {
		int cnt = 0
		input.eachLine { l -> if (HELLO.equals(l)) cnt++ }
		return cnt
	}

}
