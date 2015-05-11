package grails.plugin.extproc

import grails.transaction.Transactional
import static javax.servlet.http.HttpServletResponse.*

class ExternalProcessController {

    static final int CONFLICT   = 409
    static final int NOT_FOUND  = 404
    static final int NO_CONTENT = 204
    static final int CREATED    = 201
    static final int OK         = 200
    
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
		redirect action:defaultAction
	}

	def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ExternalProcess.list(params), model:[externalProcessInstanceCount: ExternalProcess.count()]
    }

    def show(ExternalProcess externalProcessInstance) {
        respond externalProcessInstance
    }

    def create() {
        respond new ExternalProcess(params)
    }

    @Transactional
    def save(ExternalProcess externalProcessInstance) {
        if (externalProcessInstance == null) {
            notFound()
            return
        }
        
        externalProcessInstance.env = mapFromParams(params.list("env.key"), params.list("env.value"))
    
        if (externalProcessInstance.hasErrors()) {
            respond externalProcessInstance.errors, view:'create'
            return
        }

        externalProcessInstance.save flush:true, cascade:'all'
    
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'product.label', default: 'Product'), externalProcessInstance.id])
                redirect externalProcessInstance
            }
            '*' { respond externalProcessInstance, [status: CREATED] }
        }
    }

    def edit(ExternalProcess externalProcessInstance) {
        respond externalProcessInstance
    }

    @Transactional
    def update(ExternalProcess externalProcessInstance) {        
        if (externalProcessInstance == null) {
            notFound()
            return
        }
        
        externalProcessInstance.env = mapFromParams(params.list("env.key"), params.list("env.value"))
    
        if (externalProcessInstance.hasErrors()) {
            respond externalProcessInstance.errors, view:'edit'
            return
        }

        withForm {
            externalProcessInstance.save flush:true, cascade:'all'

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'Product.label', default: 'Product'), externalProcessInstance.id])
                    redirect action:"show", id: externalProcessInstance.id
                }
                '*'{ respond externalProcessInstance, [status: OK] }
            }
        }.invalidToken {
             respond externalProcessInstance, [status: CONFLICT]
        }
    }

    private Map<String, String> mapFromParams(List<String> l1, List<String> l2) {
        def res = [:]
        l1.eachWithIndex { i, idx ->
            res[i] = l2[idx]
        }
        return res
    }

    @Transactional
    def delete(ExternalProcess externalProcessInstance) {
        if (externalProcessInstance == null) {
            notFound()
            return
        }

        externalProcessInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Product.label', default: 'Product'), externalProcessInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }


	def execute(ExternalProcess externalProcessInstance) {
		respond externalProcessInstance, model:[input: new InputCommand()]
	}

	def run(InputCommand input) {
		ExternalProcess externalProcessInstance = ExternalProcess.get(input.id)
		ExternalProcessInput procInp = new ExternalProcessInput()
		procInp.zippedWorkDir = input.zippedInput
		procInp.parameters = input.parameters
		procInp.token= input.token
		procInp.user= input.user
		ExternalProcessResult output = externalProcessService.executeProcess(input.name, procInp)
		if (output.serviceReturn?.startsWith("error")) {
			log.error "Error executing process: ${output.serviceReturn}"
			flash.message = "Error executing process: ${input.name}  - ${output.serviceReturn}"
		} else if (input.downloadZippedDir && output.zippedDir) {
			response.setContentType("application/zip")
			response.setHeader("Content-disposition","attachment; filename=${input.name}.zip")
			response.setContentLength((int)output.zippedDir.size())
			OutputStream out = response.getOutputStream()
			out.write(output.zippedDir)
			out.close()
			return
		}
		respond externalProcessInstance, model:[output: output, input:input]
	}
	
}

class InputCommand {
	String id
	String name
	String user
	String token
	byte[] zippedInput
	List<String> parameters
	boolean downloadZippedDir
	String _action_run

	@Override
	String toString() {
		"InputCommand [zippedInput=${(zippedInput?'yes':'no')}, parameters=$parameters]"
	}
}
