
package grails.plugin.extproc.remote;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the grails.plugin.extproc.remote package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ExecuteProcess_QNAME = new QName("http://extproc.plugin.grails/", "executeProcess");
    private final static QName _ExecuteProcessResponse_QNAME = new QName("http://extproc.plugin.grails/", "executeProcessResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: grails.plugin.extproc.remote
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExternalProcessInput }
     * 
     */
    public ExternalProcessInput createExternalProcessInput() {
        return new ExternalProcessInput();
    }

    /**
     * Create an instance of {@link ExternalProcessInput.Env }
     * 
     */
    public ExternalProcessInput.Env createExternalProcessInputEnv() {
        return new ExternalProcessInput.Env();
    }

    /**
     * Create an instance of {@link ExecuteProcessResponse }
     * 
     */
    public ExecuteProcessResponse createExecuteProcessResponse() {
        return new ExecuteProcessResponse();
    }

    /**
     * Create an instance of {@link ExecuteProcess }
     * 
     */
    public ExecuteProcess createExecuteProcess() {
        return new ExecuteProcess();
    }

    /**
     * Create an instance of {@link ExternalProcessResult }
     * 
     */
    public ExternalProcessResult createExternalProcessResult() {
        return new ExternalProcessResult();
    }

    /**
     * Create an instance of {@link ExternalProcessInput.Env.Entry }
     * 
     */
    public ExternalProcessInput.Env.Entry createExternalProcessInputEnvEntry() {
        return new ExternalProcessInput.Env.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteProcess }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://extproc.plugin.grails/", name = "executeProcess")
    public JAXBElement<ExecuteProcess> createExecuteProcess(ExecuteProcess value) {
        return new JAXBElement<ExecuteProcess>(_ExecuteProcess_QNAME, ExecuteProcess.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExecuteProcessResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://extproc.plugin.grails/", name = "executeProcessResponse")
    public JAXBElement<ExecuteProcessResponse> createExecuteProcessResponse(ExecuteProcessResponse value) {
        return new JAXBElement<ExecuteProcessResponse>(_ExecuteProcessResponse_QNAME, ExecuteProcessResponse.class, null, value);
    }

}
