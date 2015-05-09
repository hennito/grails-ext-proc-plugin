package grails.plugin.extproc

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestFor
import grails.test.mixin.Mock

import spock.lang.Specification
import spock.lang.Unroll


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ExternalProcess)
class ExternalProcessSpec extends Specification {
	static final String VALID = 'VALID'
    def setup() {
    	
    }

    def cleanup() {
    }

    @Unroll("test ExternalProcess all constraints #field with '#value' is '#error'")
	void "test external process constraints"() {
		given:
			def extproc = new ExternalProcess(id: 1, name:"name", tokenPattern:"abc", command:'/bin/ls')
			extproc["$field"] = value
		when:
			def validated = extproc.validate()
		then:
			validateConstraints extproc, field, error
	
		where:
			field    			  | value     	| error
			"name"   			  | null      	| "nullable"
			"name"   			  | ""        	| "blank"
			"name"   			  | "a"       	| "minSize.notmet"
			"name"   			  | "a" * 81  	| "maxSize.exceeded"
			"name"   			  | "ab"      	| VALID
			"command"			  | null      	| "nullable"
			"command"			  | ""        	| "blank"
			"command"			  | "a" * 256 	| "maxSize.exceeded"
			"command"			  | "/bin/ls" 	| VALID
			"tokenPattern"		  | null 	  	| VALID
			"tokenPattern"		  | "" 	  	  	| VALID
			"tokenPattern"		  | "a"		  	| VALID
			"tokenPattern"		  | "a" * 81  	| "maxSize.exceeded"
			"allowedFilesPattern" | "a" * 81  	| "maxSize.exceeded" 
			"returnFilesPattern"  | "a" * 81  	| "maxSize.exceeded" 
		 	"timeout"			  | 0		  	| VALID
		 	"timeout"			  | -1 		  	| 'min.notmet'
		 	"timeout"			  | 60000	  	| VALID
		 	"timeout"			  | 60001	  	| 'max.exceeded'
		 	"returnFiles"		  | null 		| VALID
		 	"returnFiles"		  | []	 		| VALID
		 	"returnFiles"		  | ["abc"]		| VALID
		 	"returnFiles"		  | ["a"] * 11	| "maxSize.exceeded" 
	}

	void validateConstraints(obj, field, error) {
       def validated = obj.validate()
       if (error && error != VALID) {
           assert !validated   
           assert obj.errors[field]
           assert error == obj.errors[field].code
       } else {
           assert !obj.errors[field]
       }
   }

	void "test name unique constraint"() {
		given:
			def extproc = new ExternalProcess(id:1, name: "name", command: "/bin/ls", tokenPattern:"abc").save(flush:true)
			def extproc2 = new ExternalProcess(id:2, name: "name", command: "/bin/cp", tokenPattern:"abc")
		when: 
			extproc2.validate()

		then:
			"unique" == extproc2.errors["name"].code

	}

}

