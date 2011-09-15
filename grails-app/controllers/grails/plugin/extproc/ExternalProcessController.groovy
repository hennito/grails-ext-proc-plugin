package grails.plugin.extproc

import java.util.Arrays;

class ExternalProcessController {

	def grailsApplication
	
	def beforeInterceptor = {
		if (!grailsApplication.config.extproc.ui.enabled)
			throw new Exception("error.extproc.ui.not.enabled")
	}
	
	
	def externalProcessService
	
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
		log.debug "index called"
        redirect(action: "list", params: params)
    }

	
	
	
    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [externalProcessInstanceList: ExternalProcess.list(params), externalProcessInstanceTotal: ExternalProcess.count()]
    }

    def create = {
        def externalProcessInstance = new ExternalProcess()
        externalProcessInstance.properties = params
        return [externalProcessInstance: externalProcessInstance]
    }

    def save = {
        def externalProcessInstance = new ExternalProcess(params)
		int idx = 0
		while (params."env.key[$idx]") {
			def key = params."env.key[$idx]"
			def value = params."env.value[$idx]"
			externalProcessInstance.env[key] = value
			idx++
		}
		
		boolean res = externalProcessInstance.validate()
		log.debug externalProcessInstance.errors
        if (res && externalProcessInstance.save(flush: true)) {            
            redirect(action: "show", id: externalProcessInstance.id)
        }
        else {
			flash.message = "error saving process"
            render(view: "create", model: [externalProcessInstance: externalProcessInstance])
        }
    }

    def show = {
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
		   
			if(domain.validate() && domain.save(flush:true)) {
			   redirect action:"show", id:domain.id
			} else {
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
	
	private def withDomain(id="id", Closure c) {
		def domain = ExternalProcess.get(params[id])
		if(domain) {
			c.call domain
		} else {
		    flash.message = "The ExternalProcess was not found."
		    redirect controller:"logout", action:"index"
		}
	}
	
	
	def execute = {  InputCommand input ->
		withDomain { domain ->			
			[externalProcessInstance: domain, input: input]
		}
	}
	
	def run = { InputCommand input ->
		log.error input
		 withDomain { domain ->
			 ExternalProcessInput procInp = new ExternalProcessInput()
			 procInp.zippedWorkDir = input.zippedInput
			 procInp.parameters = input.parameters
			 procInp.token= input.token
			 procInp.user= input.user
			 ExternalProcessResult output = externalProcessService.executeProcess(domain.name, procInp)
			 if (input.downloadZippedDir && output.zippedDir) {
				 response.setContentType("application/zip")
				 response.setHeader("Content-disposition","attachment; filename=${domain.name}.zip")
				 response.setContentLength((int)output.zippedDir.size())
				 OutputStream out = response.getOutputStream()
				 out.write(output.zippedDir)
				 out.close()
				 
			 }
			 else
			[externalProcessInstance: domain, output: output]
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
	public String toString() {
		return "InputCommand [zippedInput=" + (zippedInput?'yes':'no')+ ", parameters=" + parameters + "]";
	}	
	
}