grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	plugins {
		runtime ':jquery:1.10.0'
		runtime ':cxf:1.1.1'
		runtime ':cxf-client:1.5.3'
 		runtime ":resources:1.2"

		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
