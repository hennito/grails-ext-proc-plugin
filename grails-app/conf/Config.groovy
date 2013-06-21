extproc.ui.enabled = false // cause beforeInterceptor in controller to throw an exception

log4j = {

	root {
		info()
	}

	error 'org.codehaus.groovy.grails',
	      'org.springframework',
	      'org.hibernate',
	      'net.sf.ehcache.hibernate'

	all 'grails.plugin.extproc', 'extproc'
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
