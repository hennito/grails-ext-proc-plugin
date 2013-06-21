package grails.plugin.extproc

import grails.test.GrailsUnitTestCase

class ExternalProcessServiceTests extends GrailsUnitTestCase {

	def p1 = new ExternalProcess(id:1, name: "ab", command: "/bin/ls")
	def p2 = new ExternalProcess(id:2, name: "abc", command: "/bin/ls", )
	def p3 = new ExternalProcess(id:3, name: "invalid", command: "/bin/ls", exposedViaWS:true, isRemote:true )

	def externalProcessService = new ExternalProcessService()

	protected void setUp() {
		super.setUp()
		mockDomain(ExternalProcess,[p1,p2,p3])
		mockLogging(ExternalProcessService, true)
		mockLogging(FileHandlingService, true)
	}

	void testNoProcessFound() {
		String pName ="notfound"
		def result = externalProcessService.executeProcess(pName,null)
		assertEquals result.serviceReturn, "error.process.notfound"
	}

	void testProcessFound() {
		String pName ="ab"
		def result = externalProcessService.executeProcess(pName,null)
		assertNull result.serviceReturn
	}

	void testInvalid() {
		String pName ="invalid"
		def result = externalProcessService.executeProcess(pName,null)
		assertEquals result.serviceReturn, "error.process.remote.and.exposed"
	}
}
