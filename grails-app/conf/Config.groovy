// configuration for plugin testing - will not be included in the plugin zip

extproc.ui.enabled = false // cause beforeInterceptor in controller to throw an exception

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

	root {
		info()
	}
	
	
	error  'org.codehaus.groovy.grails.commons', // core / classloading
		'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		'org.codehaus.groovy.grails.web.mapping', // URL mapping
		'org.springframework',
		'org.hibernate',
		'net.sf.ehcache.hibernate',
		'org.codehaus.groovy.grails.web.servlet',  //  controllers
        'org.codehaus.groovy.grails.web.pages', //  GSP
		'org.codehaus.groovy.grails.plugins', // plugins
		'org.codehaus.groovy.grails.plugins.extproc' // plugins


    warn   'org.mortbay.log'
	
	all 'grails.plugin.extproc',
		'extproc'
	
}
//**********************************************************************************************
// IMPORTANT - these must be set externally to env if you want to refer to them later for use
// via cxf.  You can also simply hard code the url in the cxf section and NOT refer to a variable
// as well.
service.remote.url = ""

// set per-environment service url
environments {
	production {
		grails.serverURL = "http://www.changeme.com"
		service.remote.url = "${grails.serverURL}/services/externalProcess?wsdl"
	}
	development {
		grails.serverURL = "http://localhost:8080/${appName}"
		service.remote.url = "${grails.serverURL}/services/externalProcess?wsdl"
	}
	test {
		grails.serverURL = "http://localhost:8080/${appName}"
		service.remote.url = "${grails.serverURL}/services/externalProcess?wsdl"
		extproc.ui.enabled = true // cause beforeInterceptor in controller to throw an exception
	}
}


cxf {

	client {
		remoteInvokerServiceClient {
			clientInterface = grails.plugin.extproc.remote.ExternalProcessServicePortType
			serviceEndpointAddress = "${service.remote.url}"
		}
	}
}

grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
