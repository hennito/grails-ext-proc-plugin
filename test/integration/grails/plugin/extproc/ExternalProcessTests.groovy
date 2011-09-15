package grails.plugin.extproc

import java.io.File;
import grails.plugin.extproc.ExternalProcessInput;
import grails.plugin.extproc.ExternalProcessResult;
import grails.plugin.extproc.FileHandlingService;
import grails.plugin.extproc.ExternalProcessService;

import grails.test.*

class ExternalProcessIntegrationTests extends GroovyTestCase {
	def fileHandlingService
	def externalProcessService
    
	File temp
	
	private boolean isWindows() {
		String nameOS = "os.name";
		return System.properties.get(nameOS).toString().toUpperCase().startsWith("WINDOWS") 
	}
	
	protected void setUp() {
        super.setUp()
		//mockLogging(LocalInvokerService, true)
		//mockLogging(FileHandlingService, true)
		externalProcessService = new ExternalProcessService()
		fileHandlingService = new FileHandlingService()
		temp  = fileHandlingService.createTempDir()

		def proc1win = new ExternalProcess(
			name:'dir',
			command:"cmd",
			defaultParams:['/c','dir'],
			workDir:null,
			timeout:1000
		).save()
		def proc1 = new ExternalProcess(name:'ls',command:'/bin/ls', workDir:null).save()
		
		def proc0 = new ExternalProcess(name:'set',command:'/bin/sh', defaultParams:['-c', 'set'], workDir:null).save()
		
		def proc2 = new ExternalProcess(
			name:'pdflatex',
			command:'/usr/bin/pdflatex',
			workDir:ExternalProcess.NEW_WORKDIR,
			cleanUpWorkDir:true,
			returnZippedDir:true,
			timeout:1500,
			allowedFiles:['master.tex'],
			requiredFiles:['master.tex'],
			returnFiles:['master.pdf']).save()

		def proc3 = new ExternalProcess(
			name:'pdflatexTimeOut',
			command:'/usr/bin/pdflatex',
			workDir:ExternalProcess.NEW_WORKDIR,
			cleanUpWorkDir:true,
			timeout:1200,
			returnZippedDir:true,
			allowedFiles:['master.tex'],
			requiredFiles:[],
			returnFiles:[]).save()	
		
			
			def proc4 = new ExternalProcess(
				name:'pdflatexTokenError',
				command:'/usr/bin/pdflatex',
				workDir:ExternalProcess.NEW_WORKDIR,
				cleanUpWorkDir:true,
				tokenPattern: '123',
				timeout:1200,
				returnZippedDir:true,
				allowedFiles:['master.tex'],
				requiredFiles:[],
				returnFiles:[]).save()
		//mockDomain(ExternalProcess, [proc1,proc2,proc3, proc1win])
    }

    protected void tearDown() {
        super.tearDown()
		fileHandlingService.delDirectory (temp)
    }

	private static final String latexName = 'master.tex'
	private File createLatexInput() {
		File masterTex = fileHandlingService.fileInTemp(temp,latexName)
		masterTex << """
\\documentclass[12pt,a4paper]{report}
\\usepackage[german]{babel}
\\usepackage[utf8]{inputenc}
\\usepackage[T1]{fontenc}
\\usepackage{graphicx}
\\usepackage{multirow}
\\usepackage{amsfonts}
\\usepackage{latexsym}
\\usepackage{graphics}
\\usepackage{epsfig}
\\usepackage{longtable}
\\textwidth150mm \\oddsidemargin5mm
\\textheight230mm \\topmargin0mm
\\parindent0mm

\\setlength{\\parskip}{12pt}
\\pagestyle{headings}

\\begin{document}
\\setlength{\\baselineskip}{18pt}

\\begin{sloppypar}
\\title{ Test }
\\author{Henrik Lohse}
\\date{\\today}
\\maketitle
\\end{sloppypar}
\\end{document}
		"""
		
		return masterTex
	}
	
	void testInWIndows() {
		if (!isWindows()) { 
			println "skipping non-windows tests"
			return
		}
		ExternalProcessInput input = new ExternalProcessInput()
		input.parameters = [ "/s", "C:\\windows\\temp"]
		
		ExternalProcessResult result = externalProcessService.executeProcess('dir',input)
		assertNull result.serviceReturn
		println result?.consoleLog
		
	}
	
	
	void testFailParamsInWIndows() {
		if (!isWindows()) {
			println "skipping non-windows tests"
			return
		}
		ExternalProcessInput input = new ExternalProcessInput()
		byte[] tmC = [1,"a",3,4,5]
		String p = new String("abc\0x03") + new String(tmC) + "." 
		input.parameters = [ "/s", p ]
		
		ExternalProcessResult result = externalProcessService.executeProcess('dir',input)
		assert result.serviceReturn == "error.security.failed"
		println result?.consoleLog
		
	}
	
	void testLs() {
		if (isWindows()) {
			println "skipping non-windows tests"
			return
		}

		ExternalProcessInput input = new ExternalProcessInput()
		ExternalProcessResult result = externalProcessService.executeProcess('ls',input)
		assertNull result.serviceReturn
		assertEquals result.returnCode, 0 
		println result.consoleLog
	}
	

	void testTimeOut() {
		if (isWindows()) {
			println "skipping non-windows tests"
			return
		}
		ExternalProcessInput input = new ExternalProcessInput()		
		input.parameters = []
		
		ExternalProcessResult result = externalProcessService.executeProcess('pdflatexTimeOut',input)
		assert result.serviceReturn == "error.process.timeout"
		
		println result.consoleLog
	}
	
	void testPdfLatex() {
		if (isWindows()) {
			println "skipping non-windows tests"
			return
		}
		// set up test file
		createLatexInput()
		
		byte[] zippedInput = fileHandlingService.zipDir(temp, null)
		
		ExternalProcessInput input = new ExternalProcessInput()
		input.zippedWorkDir = zippedInput
		input.parameters = ['--interaction=nonstopmode',latexName]
		
		ExternalProcessResult result = externalProcessService.executeProcess('pdflatex',input)
		assertNull result.serviceReturn
		assertEquals result.returnCode, 0
		println result.consoleLog
		println result.zippedDir?.size()
		
    }
	
	void testSetEnv() {
		if (isWindows()) {
			println "skipping non-windows tests"
			return
		}
		ExternalProcessInput input = new ExternalProcessInput()
		input.env = ['EXTPROCTEST':'setEnv']
		
		ExternalProcessResult result = externalProcessService.executeProcess('set',input)
		println result.consoleLog
		assertNull result.serviceReturn
		
		boolean ok = false
		result.consoleLog.each { line ->
			if (line.startsWith("EXTPROCTEST") && line.contains("setEnv"))
				ok = true
		}
		
		assert ok	
	}
	
	void testTokenError() {

		ExternalProcessInput input = new ExternalProcessInput()
		ExternalProcessResult result = externalProcessService.executeProcess('pdflatexTokenError',input)
		assert result.serviceReturn == "error.token.invalid"
	}
	
	void testTokenSuccess() {
		ExternalProcessInput input = new ExternalProcessInput(token:"123")
		ExternalProcessResult result = externalProcessService.executeProcess('pdflatexTokenError',input)
		assert result.serviceReturn != "error.token.invalid"
		assert result.serviceReturn == "error.process.timeout"

	}
}
