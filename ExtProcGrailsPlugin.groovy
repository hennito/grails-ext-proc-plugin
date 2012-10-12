class ExtProcGrailsPlugin {
    // the plugin version
    def version = "0.3"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *" // cxf-plugin needs 1.3.5

    // license
    def license = "APACHE"

    // source code management
    def scm = [ url: "git://github.com/hennito/grails-ext-proc-plugin.git" ]

    // the other plugins this plugin depends on
    def dependsOn = [:] // defined in BuildConfig

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Henrik Lohse"
    def authorEmail = "henne.lohse@gmail.com"
    def title = "External Processes Plugin"
    def description = '''\
This plugin provides easy access to external processes. You can pass command line arguments and input files in zip format.
You can use this to create dvi/pdf from latex, images from gnuplot, calculation results from ansys and/or simple directory listings.

You can easily expose/consume external processes via web service making it easy to maintain only one installation of the external process and use them from your application remotely.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/hennito/grails-ext-proc-plugin"
    def issueManagement = [ url:'https://github.com/hennito/grails-ext-proc-plugin/issues' ]

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
