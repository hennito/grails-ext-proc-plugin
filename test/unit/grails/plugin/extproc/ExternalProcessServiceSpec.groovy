package grails.plugin.extproc

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestFor
import grails.test.mixin.Mock

import spock.lang.Specification
import spock.lang.Shared


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@Mock([ExternalProcess])
@TestFor(ExternalProcessService)
class ExternalProcessServiceSpec extends Specification {

    def setup() {
    	
    }

    def cleanup() {
    }

    void "test controller something"() {
    	expect:
    		1 == 1
    }

	void "test process not found"() {
		when: 
			def result = service.executeProcess("processName", new ExternalProcessInput())
		then:
			"error.process.notfound" == result.serviceReturn
	}

	void "test process found"() {
		given: 
			new ExternalProcess(id:1, name: "ab", command: "/bin/ls").save()
		when:
			def result = service.executeProcess("ab", new ExternalProcessInput(token:'quatsch'))
		then:
			!result.serviceReturn
	}


	void "test invalid process"() {
		given:
			new ExternalProcess(id:3, name: "invalid", command: "/bin/ls", exposedViaWS:true, isRemote:true ).save()
		when:
			def result = service.executeProcess("invalid", new ExternalProcessInput())
		then:
			"error.process.remote.and.exposed" == result.serviceReturn	
	}

	void "test invalid token fails"() {
		given:
			new ExternalProcess(id:1, name: "ab", command: "/bin/ls", tokenPattern:"abc").save()
		when:
			def result = service.executeProcess("ab", new ExternalProcessInput(token:""))
		then:
			"error.token.invalid" == result.serviceReturn
	}

	void "test with valid token"() {
		given:
			new ExternalProcess(id:1, name: "ab", command: "/bin/ls", tokenPattern:"abc").save()
		when:
			def result = service.executeProcess("ab", new ExternalProcessInput(token:"abc"))
		then:
			!result.serviceReturn
	}
}

