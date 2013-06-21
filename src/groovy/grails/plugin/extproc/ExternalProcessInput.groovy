package grails.plugin.extproc

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType

@XmlAccessorType(XmlAccessType.FIELD)
class ExternalProcessInput {
	String user
	String token
	List<String> parameters = []
	Map<String,String> env = [:]

	byte[] zippedWorkDir

	@Override
	String toString() {
		"ExternalProcessInput [user=$user, token=$token, parameters=$parameters, zippedWorkDir=${(zippedWorkDir!= null?zippedWorkDir.size():"null")}]"
	}
}
