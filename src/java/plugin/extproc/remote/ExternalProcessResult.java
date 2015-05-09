
package grails.plugin.extproc.remote;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for externalProcessResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="externalProcessResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="returnCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="consoleLog" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="zippedDir" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="timedOut" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="serviceReturn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "externalProcessResult", propOrder = {
    "returnCode",
    "consoleLog",
    "zippedDir",
    "timedOut",
    "serviceReturn"
})
public class ExternalProcessResult {

    protected int returnCode;
    @XmlElement(nillable = true)
    protected List<String> consoleLog;
    protected byte[] zippedDir;
    protected boolean timedOut;
    protected String serviceReturn;

    /**
     * Gets the value of the returnCode property.
     * 
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * Sets the value of the returnCode property.
     * 
     */
    public void setReturnCode(int value) {
        this.returnCode = value;
    }

    /**
     * Gets the value of the consoleLog property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the consoleLog property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConsoleLog().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getConsoleLog() {
        if (consoleLog == null) {
            consoleLog = new ArrayList<String>();
        }
        return this.consoleLog;
    }

    /**
     * Gets the value of the zippedDir property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getZippedDir() {
        return zippedDir;
    }

    /**
     * Sets the value of the zippedDir property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setZippedDir(byte[] value) {
        this.zippedDir = value;
    }

    /**
     * Gets the value of the timedOut property.
     * 
     */
    public boolean isTimedOut() {
        return timedOut;
    }

    /**
     * Sets the value of the timedOut property.
     * 
     */
    public void setTimedOut(boolean value) {
        this.timedOut = value;
    }

    /**
     * Gets the value of the serviceReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceReturn() {
        return serviceReturn;
    }

    /**
     * Sets the value of the serviceReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceReturn(String value) {
        this.serviceReturn = value;
    }

}
