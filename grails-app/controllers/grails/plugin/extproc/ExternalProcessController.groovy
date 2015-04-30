package grails.plugin.extproc

class ExternalProcessController {

	def grailsApplication

	def externalProcessService
	
	def beforeInterceptor = {
		if (!grailsApplication || !grailsApplication.config) {
			throw new Exception("no grails app and config")
		}
		if (!grailsApplication.config.extproc.ui.enabled) {
			throw new Exception("error.extproc.ui.not.enabled")
		}
	}

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	static defaultAction = 'list'

	def index() {
		redirect action:"list"
	}

	def list() {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[externalProcessInstanceList: ExternalProcess.list(params), externalProcessInstanceTotal: ExternalProcess.count()]
	}

	def create() {
		[externalProcessInstance: new ExternalProcess(params)]
	}

	def save(ExternalProcess externalProcessInstance) {
//		def externalProcessInstance = new ExternalProcess(params)
		int idx = 0
		while (params."env.key[$idx]") {
			def key = params."env.key[$idx]"
			def value = params."env.value[$idx]"
			externalProcessInstance.env[key] = value
			idx++
		}

		boolean res = externalProcessInstance.validate()
		log.debug externalProcessInstance.errors
		if (!res || !externalProcessInstance.save(flush: true)) {
			flash.message = "error saving process"
			render(view: "create", model: [externalProcessInstance: externalProcessInstance])
			return
		}

		redirect(action: "show", id: externalProcessInstance.id)
	}

	def show= {
		withDomain { domain ->
			[externalProcessInstance: domain]
		}
	}

	def edit = {
		withDomain { domain ->
			[externalProcessInstance: domain]
		}
	}

	def update = {
		withDomain { domain ->
			domain.env = [:]
			domain.allowedFiles = []
			domain.requiredFiles = []
			domain.returnFiles = []
			domain.defaultParams = []
			domain.properties = params

			int idx = 0
			while (params."env.key[$idx]") {
				def key = params."env.key[$idx]"
				def value = params."env.value[$idx]"
				domain.env[key] = value
				idx++
			}

			if (domain.validate() && domain.save(flush:true)) {
				redirect action:"show", id:domain.id
			}
			else {
				render view:"edit", model:[externalProcessInstance: domain]
			}
		}
	}

	def delete = {
		withDomain { domain ->
			domain.delete()
			redirect action:"list"
		}
	}

	private withDomain(Closure c) {
		def domain = ExternalProcess.get(params.int("id"))
		if (!domain) {
			flash.message = "The ExternalProcess was not found."
			redirect controller:"logout", action:"index"
			return
		}

		c(domain)
	}

	def execute = { InputCommand input ->
		withDomain { domain ->
			[externalProcessInstance: domain, input: input]
		}
	}

	def run = { InputCommand input ->
		withDomain { domain ->
			ExternalProcessInput procInp = new ExternalProcessInput()
			procInp.zippedWorkDir = input.zippedInput
			procInp.parameters = input.parameters
			procInp.token= input.token
			procInp.user= input.user
			ExternalProcessResult output = externalProcessService.executeProcess(domain.name, procInp)
			if (output.serviceReturn?.startsWith("error")) {
				log.error "Error executing process: ${output.serviceReturn}"
				flash.message = "Error executing process: ${domain.name}  - ${output.serviceReturn}"
			} else if (input.downloadZippedDir && output.zippedDir) {
				response.setContentType("application/zip")
				response.setHeader("Content-disposition","attachment; filename=${domain.name}.zip")
				response.setContentLength((int)output.zippedDir.size())
				OutputStream out = response.getOutputStream()
				out.write(output.zippedDir)
				out.close()
			}
			else {
				[externalProcessInstance: domain, output: output]
			}
		}
	}
}

class InputCommand {
	String user
	String token
	byte[] zippedInput
	List<String> parameters
	boolean downloadZippedDir

	@Override
	String toString() {
		"InputCommand [zippedInput=${(zippedInput?'yes':'no')}, parameters=$parameters]"
	}
}
