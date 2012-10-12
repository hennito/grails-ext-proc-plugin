package grails.plugin.extproc

import javax.jws.WebService
import javax.jws.WebParam

@WebService
public interface ExternalProcessInterface {

	public ExternalProcessResult executeProcess(
		@WebParam(name="name") String name, 
		@WebParam(name="input")ExternalProcessInput input) ;
}
