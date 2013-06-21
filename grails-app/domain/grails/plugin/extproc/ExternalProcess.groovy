package grails.plugin.extproc

class ExternalProcess {

	static final String WORKDIR_PLACEHOLDER = '__WORKDIR__'
	static final String NEW_WORKDIR = '_CREATE_NEW_'
	static final String NO_WORKDIR = '_NO_WORKDIR_'

	static constraints = {
		name minSize:2, unique:true
		command blank:false
		workDir nullable:true
		timeout min:0L
		isRemote nullable:false
		exposedViaWS()
		env nullable:true
		defaultParams nullable:true

		requiredFiles nullable:true

		allowedFilesPattern nullable:true
		allowedFiles nullable:true

		returnFilesPattern nullable:true
		returnFiles nullable:true

		tokenPattern nullable:true
	}

	static hasMany = [
		allowedFiles:String,
		requiredFiles:String,
		returnFiles:String,
		defaultParams:String
	]

	String name
	Boolean isRemote = false
	Boolean exposedViaWS = false

	String tokenPattern

	String command
	List<String> defaultParams
	String workDir = NEW_WORKDIR
	boolean cleanUpWorkDir = false
	boolean returnZippedDir = false

	Map<String, String> env = [:]
	String allowedFilesPattern
	List<String> allowedFiles = []
	List<String> requiredFiles = []
	String returnFilesPattern
	List<String> returnFiles = []

	Long timeout = 15000

	@Override
	String toString() {
		return "ExternalProcess [(id:$id) name=" + name + ", isRemote=" + isRemote +
				 ", exposedViaWS=" + exposedViaWS + ", command=" + command +
				 ", defaultParams=" + defaultParams + ", workDir=" + workDir +
				 ", cleanUpWorkDir=" + cleanUpWorkDir + ", returnZippedDir=" +
				 returnZippedDir + ", env=" + env + ", allowedFiles=" +
				 allowedFiles + ", requiredFiles=" + requiredFiles +
				 ", returnFiles=" + returnFiles + ", timeout=" + timeout + "]"
	}
}
