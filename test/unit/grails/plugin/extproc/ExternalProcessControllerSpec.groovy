package grails.plugin.extproc

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import org.codehaus.groovy.grails.web.servlet.mvc.SynchronizerTokensHolder

import spock.lang.Specification


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ExternalProcessController)
@Mock([ExternalProcess])
class ExternalProcessControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test controller something"() {
    	expect:
    		1 == 1
    }

    void "test index"() {
    	when: 
    		controller.index()
    	then:
    		"/externalProcess/list" == response.redirectedUrl
    }

    void "test list"() {
    	given:
			params.max=1
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			def p9 = new ExternalProcess(id:9, name:"beta", command:"/bin/cp").save()
		
		when:
			controller.list()

		then:
			1 == model.externalProcessList.size()
			2 == model.externalProcessInstanceCount
			p7 == model.externalProcessList[0]
	}

	void "test show"() {
		given:
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			params.id = p7.id
		when:
			controller.show()
		then:
			p7 == model.externalProcess
	}

	
	void "test create"() {
		when:
			controller.create()
		then:
			model.externalProcess instanceof ExternalProcess
	}


	void "test save success"() {
		given:
			request.method = "POST"
			request.contentType = MULTIPART_FORM_CONTENT_TYPE
			params.name = "hulle"
			params.command = "abc"
			params["env.key"] = ["key1", "key2"]
			params["env.value"] = ["val1", "val2" ]

		expect:
			0 == ExternalProcess.count()

		when:
			controller.save()

		then:			
			201 == response.status
			def externalProcess = ExternalProcess.list()[0]
			['key1':'val1', 'key2':'val2'] == externalProcess.env
	}

	void "test save success multipart form"() {		
		given:
			request.method = "POST"			
			request.contentType = FORM_CONTENT_TYPE	
			params.name = "hulle"
			params.command = "abc"
			params["env.key"] = ["key1", "key2"]
			params["env.value"] = ["val1", "val2" ]
			params.defaultParams = ['p1', 'p2', 'p3']

		expect:
			0 == ExternalProcess.count()
			
		when:
			controller.save()
			def externalProcess = ExternalProcess.list()[0]
			
		then:			
			externalProcess == model.externalProcess
			['key1':'val1', 'key2':'val2'] == externalProcess.env
			['p1', 'p2', 'p3'] == externalProcess.defaultParams
	}
	
	void "test save failure"() {
		given:
			request.method = "POST"
			params.name = ""
		
		when:
			controller.save()
		
		then:			
			model.externalProcess instanceof ExternalProcess
			"create" == view
	}


	void "test edit"() {
		given:
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			params.id = p7.id
		when:
			controller.edit()
		then:
			p7 == model.externalProcess
	}

	void "test update fails due to form token"() {
		given:
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
		and:
			params.name = "ls"
			params.command = "/bin/ls"
			params.id = p7.id
			request.method = "POST"
		
		when:
			controller.update()		
		then:
			409 == response.status

		when:
	    	def externalProcess
	    	ExternalProcess.withNewSession {
	    		externalProcess = ExternalProcess.get(p7.id)
	    	}

	    then:
			"alpha" == externalProcess.name 
	}

	void "test update failure"() {
		given:
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
		and:
			params.name = ""
			params.command = "/bin/ls"
			params.id = p7.id
			request.method = "POST"
		
		when:
			controller.update()		

		then:
			"edit" == view
			p7 == model.externalProcess

		when:
	    	def externalProcess
	    	ExternalProcess.withNewSession {
	    		externalProcess = ExternalProcess.get(p7.id)
	    	}

	    then:
			externalProcess.name == "alpha"
	}

	void "test update with valid form token"() {
		given:
			request.method = "POST"

			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			params.name = "ls"
			params.command = "/bin/ls"
			params.id = p7.id			
			params["env.key"] = ["key1", "key2"]
			params["env.value"] = ["val1", "val2" ]
			params.defaultParams = ['p1', 'p2', 'p3']

		and:
			def tokenHolder = SynchronizerTokensHolder.store(session)
	        	params[SynchronizerTokensHolder.TOKEN_URI] = '/controller/update'
	        	params[SynchronizerTokensHolder.TOKEN_KEY] = tokenHolder.generateToken(params[SynchronizerTokensHolder.TOKEN_URI])

    	when:
	    		controller.update()
	   	and:
	    		def externalProcess
		    	ExternalProcess.withNewSession {
		    		externalProcess = ExternalProcess.get(p7.id)
		    	}

    	then:
			200 == response.status	    
			"ls" == externalProcess.name 
			['p1', 'p2', 'p3'] == externalProcess.defaultParams
			['key1':'val1', 'key2':'val2'] == externalProcess.env
	}

	void "test delete"() {
		given:
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			request.method = "POST"
			params.id = p7.id
		
		when: 
			controller.delete()

		then: 
			response.status == 204
			0 == ExternalProcess.count()
	}

	void "test execute"() {
		given:
			def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			params.id = p7.id
		when:
			controller.execute()

		then:
			model.externalProcess == p7
			model.input instanceof InputCommand			
	}

	void "test execute not found"() {
		given:			
			params.id = "7"
		when:
			controller.execute()

		then:
			404 == response.status
			model.externalProcess == null
			model.input == null
	}

	void "test run"() {
		given:
        	controller.externalProcessService = [ executeProcess: { String name, Object o -> new ExternalProcessResult( zippedDir:"abc" ) } ]
        and:
        	def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			params.id = p7.id
			params.name = "alpha"
			params.downloadZippedDir = 1
		
		when:
			controller.run()

		then:
			200 == response.status
			"application/zip" == response.contentType
			3 == response.contentLengthLong
			"abc" == response.contentAsString
			model.isEmpty()

	}

	void "test run with error"() {
		given:
        	controller.externalProcessService = [ executeProcess: { String name, Object o -> new ExternalProcessResult( serviceReturn:"error.allbroken", zippedDir:"abc" ) } ]
        and:
        	def p7 = new ExternalProcess(id:7, name:"alpha", command:"/bin/ls").save()
			params.id = p7.id
			params.name = "alpha"
			params.downloadZippedDir = 1
		
		when:
			controller.run()

		then:
			200 == response.status
			p7 == model.externalProcess
			flash.message.contains("error.allbroken")

	}

}
