package grails.plugin.extproc

class ExternalProcess {

	static final String WORKDIR_PLACEHOLDER = '__WORKDIR__'
	static final String NEW_WORKDIR = '_CREATE_NEW_'
	static final String NO_WORKDIR = '_NO_WORKDIR_'

	static constraints = {
		name minSize:2, unique:true, blank:false, maxSize:80
		command blank:false, maxSize: 255
		workDir nullable:true, maxSize: 255
		timeout min:0L, max: 60000L
		isRemote nullable:false
		exposedViaWS()
		env nullable:true
		defaultParams nullable:true, maxSize: 10

		requiredFiles nullable:true, maxSize: 10

		allowedFilesPattern nullable:true, maxSize:80
		allowedFiles nullable:true, maxSize: 10

		returnFilesPattern nullable:true, maxSize: 80
		returnFiles nullable:true, maxSize: 10

		tokenPattern nullable:true, maxSize:80
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
