package grails.plugin.extproc

import javax.jws.WebParam
import javax.jws.WebService
import javax.jws.WebMethod
import javax.jws.WebResult


@WebService
interface ExternalProcessInterface {
	
	@WebMethod(operationName="executeProcess")
	@WebResult(name="result")	
	ExternalProcessResult executeProcess(
		@WebParam(name="name") String name,
		@WebParam(name="input")ExternalProcessInput input)
}
