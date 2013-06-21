package grails.plugin.extproc

import javax.jws.WebParam
import javax.jws.WebService

@WebService
interface ExternalProcessInterface {

	ExternalProcessResult executeProcess(
		@WebParam(name="name") String name,
		@WebParam(name="input")ExternalProcessInput input)
}
