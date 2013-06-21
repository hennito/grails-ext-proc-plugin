package grails.plugin.extproc

import grails.test.GrailsUnitTestCase

class ExternalProcessTests extends GrailsUnitTestCase {

	def p1 = new ExternalProcess(name: "ab", command: "/bin/ls")
	def p2 = new ExternalProcess(name: "abc", command: "/bin/ls")

	protected void setUp() {
		super.setUp()
		mockForConstraintsTests(ExternalProcess, [ p1, p2 ])
	}

	void testConstraintsName() {
		p1.name= null
		assertFalse p1.validate()
		assertEquals "nullable", 	p1.errors["name"]

		p1.name= "a"
		assertFalse p1.validate()
		assertEquals "minSize", 	p1.errors["name"]

		p1.name= "ab"
		assertTrue p1.validate()

		p2.name="ab"
		assertFalse p2.validate()
		assertEquals "unique", 	p2.errors["name"]
	}

	void testConstraintsCommand() {
		p1.command = null
		assertFalse p1.validate()
		assertEquals "nullable", 	p1.errors["command"]

		p1.command = ""
		assertFalse p1.validate()
		assertEquals "blank", 	p1.errors["command"]

		p1.command="ls"
		assertTrue p1.validate()
	}

	void testConstraintsTimeout() {
		p1.timeout = null
		assertFalse p1.validate()
		assertEquals "nullable", 	p1.errors["timeout"]

		p1.timeout = 0
		assertTrue p1.validate()

		p1.timeout = -2
		assertFalse p1.validate()
		assertEquals "min", 	p1.errors["timeout"]

		p1.timeout = 1e6
		assertTrue p1.validate()
	}
}
