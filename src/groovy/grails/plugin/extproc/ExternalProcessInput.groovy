package grails.plugin.extproc

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
class ExternalProcessInput {
	String user
	String token
	List<String> parameters = []
	Map<String,String> env = [:]
	
	byte[] zippedWorkDir

	@Override
	public String toString() {
		return "ExternalProcessInput [user=" + user + ", token=" + token + ", parameters=" + parameters + ", zippedWorkDir=" + (zippedWorkDir!= null?zippedWorkDir.size():"null") + "]";
	}
}
