grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
    	grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
	test "com.h2database:h2:1.0.20061217"
	}

    plugins {
		runtime ':jquery:1.8.3'
		runtime ':cxf:1.1.1'			
		runtime ':cxf-client:1.5.3'
 		runtime ":resources:1.2"
		build ":release:2.0.4"
	}
    
}
