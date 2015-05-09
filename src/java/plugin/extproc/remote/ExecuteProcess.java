
package grails.plugin.extproc.remote;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for executeProcess complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="executeProcess">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="input" type="{http://extproc.plugin.grails/}externalProcessInput" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executeProcess", propOrder = {
    "name",
    "input"
})
public class ExecuteProcess {

    protected String name;
    protected ExternalProcessInput input;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the input property.
     * 
     * @return
     *     possible object is
     *     {@link ExternalProcessInput }
     *     
     */
    public ExternalProcessInput getInput() {
        return input;
    }

    /**
     * Sets the value of the input property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExternalProcessInput }
     *     
     */
    public void setInput(ExternalProcessInput value) {
        this.input = value;
    }

}
