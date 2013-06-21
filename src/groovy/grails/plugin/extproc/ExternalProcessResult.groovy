package grails.plugin.extproc

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType

@XmlAccessorType(XmlAccessType.FIELD)
class ExternalProcessResult {
	int returnCode = 0
	List<String> consoleLog = []
	byte[] zippedDir
	boolean timedOut = false

	String serviceReturn

	@Override
	String toString() {
		"ExternalProcessResult [returnCode=$returnCode, consoleLog=$consoleLog, zippedDir=${(zippedDir!=null?zippedDir.size():"null")}, timedOut=$timedOut]"
	}
}
