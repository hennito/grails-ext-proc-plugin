grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		//test 'org.gmock:gmock:0.8.0'
        //test 'org.hamcrest:hamcrest-library:1.1' // Optionally, you can use hamcrest matchers
    	test 'junit:junit:4.11'
  		test 'org.hamcrest:hamcrest-all:1.3'
    	test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
  	}

	plugins {
		runtime ':jquery:1.11.1'
		compile ':cxf:2.0.1'
		compile ':cxf-client:2.1.1'
		compile ":asset-pipeline:2.1.5"

		build ':release:3.1.1', ':rest-client-builder:1.0.3', {
			export = false
		}

		test(":spock:0.7") {
      		exclude "spock-grails-support"
    	}
	}
}
