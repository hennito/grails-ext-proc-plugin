package grails.plugin.extproc

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin

import org.junit.Before
import org.junit.Test

@TestFor(ExternalProcessController)
@TestMixin(DomainClassUnitTestMixin)
class ExternalProcessControllerTests {

	def p7
	def p9

	@Before
	void setUp() {
		config.extproc.ui.enabled = true

		mockDomain(ExternalProcess, [])
		p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls")
		p9 = new ExternalProcess(id:9, name:"beta", command:"/bin/cp")

		p7.save()
		p9.save()
	}

	@Test
	void index() {
		controller.index()
		assertEquals "/externalProcess/list", response.redirectedUrl
	}

	@Test
	void list() {
		params.max=10
		def m1 = controller.list()
		assertEquals 2, m1.externalProcessInstanceList.size()
		assertEquals p7, m1.externalProcessInstanceList[0]
		assertEquals p9, m1.externalProcessInstanceList[1]
		assertEquals 2, m1.externalProcessInstanceTotal
	}

	@Test
	void show() {
		params.id = p7.id
		def m1 = controller.show()
		assertEquals p7, m1.externalProcessInstance
	}

	@Test
	void create() {
		def model = controller.create()
		assertTrue model.externalProcessInstance instanceof ExternalProcess
	}

	@Test
	void save_success() {
		request.method = "POST"
		params.name = "hulle"
		params.command = "abc"
		controller.save()
		assertEquals 0, response.redirectedUrl.indexOf("/externalProcess/show")
		//assertNotNull != controller.redirectArgs.id
	}

	@Test
	void save_failure() {
		request.method = "POST"
		params.name = ""
		def m1 = controller.save()
		assertEquals "error saving process", flash.message
		assertEquals "/externalProcess/create", view
		assertTrue  model.externalProcessInstance instanceof ExternalProcess
	}

	@Test
	void edit() {
		params.id = p9.id
		def m1 = controller.edit()
		assertEquals p9, m1.externalProcessInstance
	}

	@Test
	void update_success() {
		params.name = "ls"
		params.command = "/bin/ls"
		params.id = p7.id
		request.method = "POST"
		controller.update()
		assertEquals "/externalProcess/show/${p7.id}", response.redirectedUrl
	}

	@Test
	void update_failure() {
		request.method = "POST"
		params.name = ""
		params.command = null
		params.id = p9.id
		controller.update()
		assertEquals "/externalProcess/edit", view
		assertEquals p9, model.externalProcessInstance
	}

	@Test
	void delete() {
		request.method = "POST"
		params.id = p7.id
		def m1 = controller.delete()

		assertEquals "/externalProcess/list", response.redirectedUrl
		assertEquals 1, ExternalProcess.count()
	}

	@Test
	void withExternalProcess_success() {
		params.id = p7.id
		def externalProcess = null
		controller.withDomain { p ->
			externalProcess = p
		}

		assertEquals p7.id, externalProcess.id
	}

	@Test
	void withExternalProcess_fail() {
		params.id = 0
		controller.withDomain { p ->
			assert false
		}
		assertEquals "The ExternalProcess was not found.", controller.flash.message
		assertEquals "/logout/index", response.redirectedUrl
	}
}
