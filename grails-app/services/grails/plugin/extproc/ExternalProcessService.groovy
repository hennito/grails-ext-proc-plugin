package grails.plugin.extproc

import java.util.Map;

import java.util.List;
import grails.plugin.extproc.ExternalProcessInput;
import grails.plugin.extproc.ExternalProcessResult;
import java.util.regex.Pattern
import com.grails.cxf.client.*

class ExternalProcessService {
	static expose = ['cxf']
	static exclude = ["invokeRemote","invokeLocal"]
	
	def fileHandlingService
	def webServiceClientFactory

	static transactional = true

	ExternalProcessResult executeProcess(String name, ExternalProcessInput input) {
		final String METHOD_NAME = "executeProcess() - "
		log.trace "$METHOD_NAME entering ..."
		log.info "$METHOD_NAME process name is $name"
		ExternalProcess process = ExternalProcess.findByName(name)
		
		if (process)
			if (process.isRemote)
				if (!process.exposedViaWS) 
					return invokeRemote((ExternalProcess)process,input)
				else 
					return new ExternalProcessResult(serviceReturn:'error.process.remote.and.exposed')
			else 
				return invokeLocal((ExternalProcess)process,input)
		else
			return new ExternalProcessResult(serviceReturn:'error.process.notfound')
	}

	private ExternalProcessResult invokeRemote(ExternalProcess process, ExternalProcessInput input) {
		def result

		final String METHOD_NAME = "invokeRemote() - "
		log.info "$METHOD_NAME process is $process"
		log.debug "$METHOD_NAME input is $input"
		
		def (command, url) = process.command.split("@")
		log.info "remote command is $command"
		log.info "remote url is $url"

		DynamicWebServiceClient client = new DynamicWebServiceClient(
				clientInterface: grails.plugin.extproc.remote.ExternalProcessServicePortType,
				serviceName: "$command",
				serviceEndpointAddress: "$url",
				secured: false,
				username: "${input.user}",
				password: "${input.token}",
				webServiceClientFactory: webServiceClientFactory)
		
	

/*
		Object webServiceClient = webServiceClientFactory.getWebServiceClient(
			grails.plugin.extproc.remote.ExternalProcessServicePortType, 
			"$command", 
			"$url", 
			false, 
			"testUser", 
			"testPassword"
		)
		
	*/	
		
	def webserviceObject = client.object
	log.info "webserviceObject : $webserviceObject"
	
	log.info webServiceClientFactory.interfaceMap
		
		log.info "wsClient is $client"
		
		grails.plugin.extproc.remote.ExternalProcessInput wrappedInput = 
			new grails.plugin.extproc.remote.ExternalProcessInput(
				user:input.user,
				token:input.token,
				parameters:input.parameters,
				env:input.env,
				zippedWorkDir:input.zippedWorkDir
			)
		
		 
		result = webserviceObject.executeProcess(command, wrappedInput)
//	
		
		grails.plugin.extproc.ExternalProcessResult wrappedResult = 
		new grails.plugin.extproc.ExternalProcessResult(
			returnCode:result.returnCode,
			consoleLog:result.consoleLog,
			zippedDir:result.zippedDir,
			serviceReturn:result.serviceReturn
		)
		
		return wrappedResult	
	}
	
	private ExternalProcessResult invokeLocal(ExternalProcess process, ExternalProcessInput input) {
		final String METHOD_NAME = "invokeLocal() - "
		
		log.info "$METHOD_NAME process is $process"
		log.debug "$METHOD_NAME input is $input"
		
		ExternalProcessResult result = new ExternalProcessResult()
		
		if (!fileHandlingService) fileHandlingService = new FileHandlingService()

		if (!process) {
			log.error "$METHOD_NAME process not found"
			result.serviceReturn = "error.process.notfound"
			return result
		}

		if (process.tokenPattern) {
			if (!(process.tokenPattern =~ input.token )) {
				log.error "$METHOD_NAME invalid token provided: ${input.token}"
			result.serviceReturn = "error.token.invalid"
			return result
			}
				
		}
		
		if (process.isRemote) {
			log.error "$METHOD_NAME attempt to call remote process locally"
			result.serviceReturn = "error.process.local.not.remote"
			return result

		}

		// setup workdir
		File workDir
		log.debug "$METHOD_NAME process.workDir is ${process.workDir}"
		switch (process.workDir) {
			case null:
				break
			case ExternalProcess.NO_WORKDIR:
				process.workDir = null
			case ExternalProcess.NEW_WORKDIR:
				workDir = fileHandlingService.createTempDir()
				break
			default:
				workDir = new File(process.workDir)
				if (!workDir.exists()) {
					workdir = fileHandlingService.createTempDir(process.workDir)
				}
				break
		}


		List<String> cmds = new ArrayList<String>()
		cmds.add(process.command)
		if (process.defaultParams)
			process.defaultParams.each { pr ->
				cmds.add(workDir?pr.replaceAll(ExternalProcess.WORKDIR_PLACEHOLDER, workDir.absolutePath):pr)
			}
		if (input && input.parameters)
			input.parameters.each { param ->
				if (fileHandlingService.basicSecurityCheck(param))
					cmds.add(workDir?param.replaceAll(ExternalProcess.WORKDIR_PLACEHOLDER, workDir.absolutePath):param)
				else {
					log.error "$METHOD_NAME security check failed for '$param'"
					result.serviceReturn = "error.security.failed"
					return result
				}

			}
		log.debug "$METHOD_NAME cmds are $cmds"

		ProcessBuilder pb = new ProcessBuilder(cmds)
		pb.redirectErrorStream (true)

		// set needed env vars
		Map<String,String> env = pb.environment();
		if (input)
			input.env.each { key, value ->
				env.put(key,value)
			}
		process.env.each { key, value ->
			env.put(key, value)
		}

		pb.directory(workDir);

		log.debug "$METHOD_NAME workDir is $workDir"

		if (workDir != null)
			if (input && input.zippedWorkDir) {
				log.debug "$METHOD_NAME unzipping provided inputfiles ..."
				def allFiles = []
				fileHandlingService.unzipByteArrayToDir(input.zippedWorkDir, workDir) { fn ->
					boolean restricted = process.allowedFiles || process.requiredFiles || process.allowedFilesPattern
					boolean dumpFileByAllowed = restricted && (!process.allowedFiles || process.allowedFiles.contains(fn))
					boolean dumpFileByRequired = restricted &&  (!process.requiredFiles || process.requiredFiles.contains(fn))
					
					Pattern pat = process.allowedFilesPattern?Pattern.compile(process.allowedFilesPattern): null
					boolean dumpFileByRegEx = restricted && (!pat || fn.matches(pat)) 
						
					boolean fileAllowed = !restricted || dumpFileByAllowed || dumpFileByRequired || dumpFileByRegEx
					if (fileAllowed)
						allFiles << fn 				
					return fileAllowed
				}
				boolean failure = false
				process.requiredFiles.each { rf ->
					if (!allFiles.contains(rf)) failure = true
				}
				if (failure)
					throw new Exception("input not complete")
			}
			else
				log.debug "$METHOD_NAME no input files provided"
		else
		if (input && input.zippedWorkDir)
			log.error "$METHOD_NAME no workdir, cannot unzip !"
		else
			log.debug "$METHOD_NAME no workdir, no input files ... ok"

		Process proc = null;
		try {
			log.info "$METHOD_NAME starting process ${process.name} for user ${input?.user} with token ${input?.token}"

			

			Long start = System.currentTimeMillis()

			Worker worker = new Worker(pb.start());
			worker.start();

			log.debug "$METHOD_NAME worker started"

			boolean timedOut = false
			try {
				if (process.timeout) {
					log.debug "$METHOD_NAME setting timeout to ${process.timeout}"
					worker.join(process.timeout);
				}
				else
					worker.join();

				log.debug worker.consoleLog
				result.consoleLog = worker.consoleLog
				log.debug "$METHOD_NAME exit code is ${worker.exit}"

				if (worker.exit != null) {
					result.returnCode= worker.exit
				}
				else {
					log.error "$METHOD_NAME process ${process.name} timed out. no results."
					result.serviceReturn = "error.process.timeout"
				}
			} catch(InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
				log.error "$METHOD_NAME finished process ${process.name}: Thread interrupted:$ex"
				throw ex;
			} finally {
				worker.process.destroy();
			}

			//
			long stop =  System.currentTimeMillis()
			log.info "$METHOD_NAME finished process ${process.name}, took ${stop - start} ms."

			if (workDir && process.returnZippedDir) {
				log.info "zipping ${process.returnFiles} and/or ${process.returnFilesPattern}"
				
				result.zippedDir = fileHandlingService.zipDir(workDir) { fn ->  
					( !process.returnFiles || process.returnFiles.contains(fn) ) && (!process.returnFilesPattern || fn =~ "${process.returnFilesPattern}") 
				}
			}
			if (workDir && process.cleanUpWorkDir) {
				log.info "$METHOD_NAME cleaning up temp workdir $workDir"
				fileHandlingService.delDirectory workDir
			}

			log.debug "$METHOD_NAME done. $result"
			return result
		} catch (Exception ex1) {
			ex1.printStackTrace()
			log.error "$METHOD_NAME had an error: $ex1"			
			throw ex1
		}

		log.trace "$METHOD_NAME done."
	}

}


private  class Worker extends Thread {
	private final Process process;
	private Integer exit;
	private List<String> consoleLog =new ArrayList<String>();
	private Worker(Process process) {
		this.process = process;
	}
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(process.inputStream);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				consoleLog << line
			}
			exit = process.waitFor();
		} catch (InterruptedException ignore) {
			return;
		}
	}
}
