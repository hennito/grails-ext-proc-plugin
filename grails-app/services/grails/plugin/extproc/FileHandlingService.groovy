package grails.plugin.extproc

import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

import java.io.*

class FileHandlingService {
	private static final int BUFFER = 4096

	static transactional = true

	
	boolean basicSecurityCheck(String p) {
		boolean res = true
		if (p =~ /^[a-zA-Z0-9\/\\\.]*$/)
			return true
		if (p.contains("&")) return false;
		if (p.contains("|")) return false;
		if (p.contains(">")) return false;
		if (p.contains("<")) return false;
		if (p.contains("..")) return false;


		return res
	}
	
	
	public void delDirectory(File path) {
		log.info "deleting directory $path"
		File cur = path
		if (cur.exists() ) {
			if (cur.isDirectory())
				cur.list().toList().each { fn ->
					def f = new File(cur.absolutePath.toString() + "/" + fn)
					if (f.isDirectory()) {
						delDirectory(f)
					}
					log.debug "del file ${f.absolutePath}"
					assert f.delete()
				}
			log.debug "--- $cur ---"
			assert cur.delete()
		}
		else
			log.error "$path does not exist"
	}

	File fileInTemp(File temp, String name) {
		return new File(temp.absolutePath + "/" + name)

	}
	File createTempDir(String path) {
		// create temp dir
		File temp
		try {
			log.debug "generating new temp dir"
			if (!path) {
				temp = File.createTempFile("ext-proc", Long.toString(System.nanoTime()))
				temp.delete()
				temp.mkdir()
			}
			else {
				temp = new File(path)
				if (!temp.exists() || !temp.isDirectory())
					throw new Exception("Path $path does not exist or is not a directory!")
				else
					log.info "temp $path existed."
			}
			if (temp.exists() && temp.isDirectory()) {
				log.info "temp dir created: ${temp}"
			}

		} catch(Exception ex) {
			log.error "Error creating temp dir:$ex"
			throw ex
		}

		return temp
	}


	private static void dumpZipFileEntr(ZipFile zf, ZipEntry ze, File path)  throws IOException  {
		InputStream istr = zf.getInputStream(ze);
		BufferedInputStream bis = new BufferedInputStream(istr);
		String fn = ze.getName().replace("\\","/")
		File output = new File(path.absolutePath + "/"  + fn.split("/")[-1])
		FileOutputStream fos = new FileOutputStream(output);
		int sz = (int)ze.getSize();
		byte[] buf= new byte[BUFFER];
		int ln = 0;
		while (sz > 0 &&  // workaround for bug
		(ln = bis.read(buf, 0, Math.min(BUFFER, sz))) != -1) {
			fos.write(buf, 0, ln);
			sz -= ln;
		}
		bis.close();
		fos.flush();
	}


	private void dumpZipFileEntry(ZipInputStream bis, ZipEntry ze, File path) {
		String fn = ze.getName().replace("\\","/")
		File output = new File(path.absolutePath + "/"  + fn.split("/")[-1])
		FileOutputStream fos = new FileOutputStream(output);

		int sz = (int)ze.getSize();

		byte[] buf= new byte[BUFFER];
		int ln = 0;

		long total = 0

		while ((bis.available()) != 0) {
			log.trace "reading ... "
			int bytesRead = bis.read(buf, 0, BUFFER)
			if (bytesRead > 0) {
				log.trace "read $bytesRead bytes"
				fos.write(buf, 0, bytesRead);
				total += bytesRead
				log.trace "total is $total"
			}
		}
		log.debug "dumped $fn from zip to $output, size is $total"
		fos.flush();
		fos.close();
	}
	
	void unzipByteArrayToDir(byte[] file, File path, Closure c) {
		ZipInputStream  zis = new ZipInputStream(new ByteArrayInputStream(file));
		ZipEntry entry;
		List<String> provided = []

		while ((entry = zis.getNextEntry()) != null) {
			String fn = entry.getName().replace("\\","/").split("/")[-1]

			boolean allowedByClosure = (c == null || c( fn ))

			if (allowedByClosure)
				dumpZipFileEntry(zis, entry, path)
			else
				log.info "not allowed $fn in input - skipped"
		}

	}

	void unzipFileToDir(File file, File path, Closure c) {
		byte[] array = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(array);
		
			unzipByteArrayToDir(array, path, c )
		}
		catch (Exception ex) {
			throw new RuntimeException(ex)
		}
	}
	
	byte[] zipDir(File dir, Closure c) {
		if (dir && dir.exists()) {
			try {
				ByteArrayOutputStream dest = new ByteArrayOutputStream(  )
				ZipOutputStream zip = new ZipOutputStream(new  BufferedOutputStream(dest,BUFFER));

				addDirectoryToZip(zip, dir, c)
				zip.close();
				return dest.toByteArray();
			}
			catch (java.util.zip.ZipException zex) {
				log.error "zipDirInclude: $zex"
				return null
			}

		}
		else
			return null
	}

	void addDirectoryToZip(ZipOutputStream zip, File path, Closure c) {
		final String METHOD_NAME = "addDirectoryToZip -"
		log.debug "$METHOD_NAME path is: ${path.absolutePath}"

		def files = path.list().toList()
		log.debug "$METHOD_NAME files are: ${files}"

		byte[] data = new byte[BUFFER];
		if (files)
			files.each { filename ->
				boolean allowedByClosure = (c == null || c( filename ))
				if (allowedByClosure) {
					File file = fileInTemp(path,filename)
					// 	skip hidden files and ., ..
					if (!file.toString().startsWith(".")) {
						if (!file.isDirectory()) {
							def b= new FileInputStream(file).getBytes();
							log.info "$METHOD_NAME Adding file to result zip: $file, size is ${b.size()}"

							ByteArrayInputStream fi = new ByteArrayInputStream(b);
							def origin = new BufferedInputStream(fi, BUFFER);
							ZipEntry entry = new ZipEntry(filename);
							entry.setSize (b.size())
							zip.putNextEntry(entry);

							int count;
							while((count = origin.read(data, 0, BUFFER)) != -1) {
								zip.write(data, 0, count);
							}
							origin.close();
						} else {
							addDirectoryToZip(zip, file)
						}
					}
				} else {
					log.debug "$METHOD_NAME skipped $filename"
				}
			}
	}

}

