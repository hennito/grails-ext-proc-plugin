
package grails.plugin.extproc.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for executeProcessResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="executeProcessResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://extproc.plugin.grails/}externalProcessResult" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executeProcessResponse", propOrder = {
    "result"
})
public class ExecuteProcessResponse {

    protected ExternalProcessResult result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ExternalProcessResult }
     *     
     */
    public ExternalProcessResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExternalProcessResult }
     *     
     */
    public void setResult(ExternalProcessResult value) {
        this.result = value;
    }

}
