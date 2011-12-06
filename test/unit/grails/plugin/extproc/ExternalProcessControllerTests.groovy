package grails.plugin.extproc

import grails.test.*
import org.junit.*


class ExternalProcessControllerTests extends ControllerUnitTestCase {

    def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls")
    def p9 = new ExternalProcess(id:9, name:"beta", command:"/bin/cp")

    @Before
    public void setUp() {
        super.setUp()
    	mockDomain ExternalProcess, [p7,p9]
    }

    @After
    public void tearDown() {
        super.tearDown()
    }

    @Test
    public void index() {
		controller.index()
		assert "list" == controller.redirectArgs.action
    }

/*
    @Test
    public void list() {
    	def model = controller.list()
    	assert 2 == model.externalProcessInstanceList.size()
    	assert p7 == model.externalProcessInstanceList[0]
    	assert p9 == model.externalProcessInstanceList[1]
    	assert 2 == model.externalProcessInstanceTotal
    }
*/
    @Test
    public void show() {
    	controller.params.id = 7
    	def model = controller.show()
    	assert p7 == model.externalProcessInstance
    }

    @Test
    public void create() {
    	def model = controller.create()
    	assert model.externalProcessInstance instanceof ExternalProcess
    }

    @Test
    public void save_success() {
		controller.params.name = "Paul Woods"
		controller.params.command = "abc"
    	controller.save()
    	assert "show" == controller.redirectArgs.action
    	assert null != controller.redirectArgs.id
    }

    @Test
    public void save_failure() {
    	controller.params.name = ""
    	controller.save()
    	assert "create" == controller.renderArgs.view
    	assert controller.renderArgs.model.externalProcessInstance instanceof ExternalProcess
    }

    @Test
    public void edit() {
    	controller.params.id = 9
    	def model = controller.edit()
    	assert p9 == model.externalProcessInstance
    }

    @Test
    public void update_success() {
		controller.params.name = "ls"
		controller.params.command = "/bin/ls"
    	controller.params.id = 7
    	controller.update()
    	assert "show" == controller.redirectArgs.action
    	assert 7 == controller.redirectArgs.id
    }

    @Test
    public void update_failure() {
    	controller.params.name = ""
		controller.params.command = null
    	controller.params.id = 9
    	controller.update()
    	assert "edit" == controller.renderArgs.view
    	assert controller.renderArgs.model.externalProcessInstance instanceof ExternalProcess
    }

    @Test
    public void delete() {
    	controller.params.id = 7
    	controller.delete()
    	assert "list" == controller.redirectArgs.action
    	assert 1 == ExternalProcess.count()
    }

    @Test
    public void withExternalProcess_success() {
    	controller.params.id = 7
    	def externalProcess = null
    	controller.withDomain() { p ->
    		externalProcess = p
    	}

    	assert 7 == externalProcess.id
    }

    @Test
    public void withExternalProcess_fail() {
    	controller.params.id = 0
    	controller.withDomain() { p ->
    		assert false
    	}
	assert "The ExternalProcess was not found." == controller.flash.message
	assert "index" == controller.redirectArgs.action
	assert "logout" == controller.redirectArgs.controller
   }

}
