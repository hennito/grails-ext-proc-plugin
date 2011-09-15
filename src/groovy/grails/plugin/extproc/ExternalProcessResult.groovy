package grails.plugin.extproc

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
class ExternalProcessResult {
	int returnCode = 0
	List<String> consoleLog = []
	byte[] zippedDir = null
	boolean timedOut = false

	String serviceReturn = null

	@Override
	public String toString() {
		return "ExternalProcessResult [returnCode=" + returnCode + ", consoleLog=" + consoleLog + ", zippedDir=" + (zippedDir!=null?zippedDir.size():"null") + ", timedOut=" + timedOut + "]";
	}	
}
