class ExtProcGrailsPlugin {
    def version = "0.4"

    def grailsVersion = "1.3.5 > *" // cxf-plugin needs 1.3.5

    def license = "APACHE"

    def scm = [ url: "git://github.com/hennito/grails-ext-proc-plugin.git" ]

    def loadBefore = ['cxfClient', 'cxf']

    def author = "Henrik Lohse"
    def authorEmail = "henne.lohse@gmail.com"
    def title = "External Processes Plugin"
    def description = '''\
This plugin provides easy access to external processes. You can pass command line arguments and input files in zip format.
You can use this to create dvi/pdf from latex, images from gnuplot, calculation results from ansys and/or simple directory listings.

You can easily expose/consume external processes via web service making it easy to maintain only one installation of the external process and use them from your application remotely.
'''

    def documentation = "https://github.com/hennito/grails-ext-proc-plugin"
    def issueManagement = [ url:'https://github.com/hennito/grails-ext-proc-plugin/issues' ]

    def doWithSpring = { 
        application.config.cxf.client.remoteInvokerServiceClient.clientInterface = grails.plugin.extproc.remote.ExternalProcessServicePortType
        application.config.cxf.client.remoteInvokerServiceClient.serviceEndpointAddress = "configMe"

        application.config.cxf.servlet.loadOnStartup = 10
        application.config.cxf.servlet.servlets = [ CxfServlet: '/services/*' ]
        application.config.cxf.servlet.endpoint.soap12Binding = false
    }
}
