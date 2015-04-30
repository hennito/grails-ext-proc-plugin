package grails.plugin.extproc

import java.util.regex.Pattern

import javax.jws.WebParam
import javax.jws.WebService
import javax.jws.WebResult
import javax.jws.WebMethod

import com.grails.cxf.client.DynamicWebServiceClient
import com.grails.cxf.client.WebServiceClientFactory
import org.grails.cxf.utils.GrailsCxfEndpoint
import org.grails.cxf.utils.EndpointType

import grails.transaction.Transactional

@Transactional
@GrailsCxfEndpoint(address='/externalProcess', expose=EndpointType.JAX_WS)
class ExternalProcessService implements ExternalProcessInterface  {

	def grailsApplication

	def fileHandlingService
	//def remoteInvokerServiceClient #### NOTE: injection breaks it all
	def webServiceClientFactory
	
	

	@WebMethod
	@WebResult(name='')
	ExternalProcessResult executeProcess(
		@WebParam(name="name") String name, 
		@WebParam(name="input") ExternalProcessInput input)	
	{
		final String METHOD_NAME = "executeProcess() - "
		log.trace "$METHOD_NAME entering ..."
		log.debug "$METHOD_NAME process name is $name"
		ExternalProcess process = ExternalProcess.findByName(name)

		if (process) {

			if (process.tokenPattern) {
				
				if (!input.token.matches(process.tokenPattern)) {
					log.error "$METHOD_NAME invalid token provided: '${input.token}' / '${process.tokenPattern}'"
					return new ExternalProcessResult(serviceReturn: "error.token.invalid")
				}
			}

			if (process.isRemote) {
				if (!process.exposedViaWS) {
					return invokeRemote(process, input)
				}
				return new ExternalProcessResult(serviceReturn:'error.process.remote.and.exposed')
			}
			return invokeLocal((ExternalProcess)process,input)
		}
		return new ExternalProcessResult(serviceReturn:'	error.process.notfound')
	}

	
	private ExternalProcessResult invokeRemote(ExternalProcess process, ExternalProcessInput input) {
		final String METHOD_NAME = "invokeRemote() - "
		log.info "$METHOD_NAME process is $process"
		log.debug "$METHOD_NAME input is $input"	
		
		def (command, url) = process.command.split("@")
		log.info "remote command is $command"
		log.info "remote url is $url"

		def ctx = grailsApplication.mainContext

		def wsClient = ctx.getBean('remoteInvokerServiceClient')
		webServiceClientFactory = ctx.getBean("webServiceClientFactory")
		webServiceClientFactory.updateServiceEndpointAddress('remoteInvokerServiceClient', url)


		def wrappedInput =  new grails.plugin.extproc.remote.ExternalProcessInput(
			user:input.user,
			token:input.token,
			parameters:input.parameters,
			env:input.env,
			zippedWorkDir:input.zippedWorkDir)


		def result = wsClient.executeProcess(command, wrappedInput)

		ExternalProcessResult wrappedResult = new ExternalProcessResult(
			returnCode:result?.returnCode,
			consoleLog:result?.consoleLog,
			zippedDir:result?.zippedDir,
			serviceReturn:result?.serviceReturn
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

		List<String> cmds = []
		cmds.add(process.command)
		process.defaultParams.each { pr ->
			cmds.add(workDir?pr.replaceAll(ExternalProcess.WORKDIR_PLACEHOLDER, workDir.absolutePath):pr)
		}
		input?.parameters?.each { param ->
			if (fileHandlingService.basicSecurityCheck(param)) {
				cmds.add(workDir?param.replaceAll(ExternalProcess.WORKDIR_PLACEHOLDER, workDir.absolutePath):param)
			}
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
		Map<String,String> env = pb.environment()
		input?.env?.each { key, value ->
			env.put(key,value)
		}
		process.env.each { key, value ->
			env.put(key, value)
		}

		pb.directory(workDir)

		log.debug "$METHOD_NAME workDir is $workDir"

		if (workDir) {
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
					if (fileAllowed) {
						allFiles << fn
					}
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
		}
		else {
			if (input && input.zippedWorkDir) {
				log.error "$METHOD_NAME no workdir, cannot unzip !"
			}
			else {
				log.debug "$METHOD_NAME no workdir, no input files ... ok"
			}
		}

		Process proc
		try {
			log.info "$METHOD_NAME starting process ${process.name} for user ${input?.user} with token ${input?.token}"

			Long start = System.currentTimeMillis()

			Worker worker = new Worker(pb.start())
			worker.start()

			log.debug "$METHOD_NAME worker started"

			boolean timedOut = false
			try {
				if (process.timeout) {
					log.debug "$METHOD_NAME setting timeout to ${process.timeout}"
					worker.join(process.timeout)
				}
				else {
					worker.join()
				}

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
				worker.interrupt()
				Thread.currentThread().interrupt()
				log.error "$METHOD_NAME finished process ${process.name}: Thread interrupted:$ex"
				throw ex
			} finally {
				worker.process.destroy()
			}

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
			log.error "$METHOD_NAME had an error: $ex1"
			throw ex1
		}

		log.trace "$METHOD_NAME done."
	}

	class Worker extends Thread {
		private final Process process
		private Integer exit
		private List<String> consoleLog = []
		private Worker(Process process) {
			this.process = process
		}
		void run() {
			try {
				InputStreamReader isr = new InputStreamReader(process.inputStream)
				BufferedReader br = new BufferedReader(isr)
				String line
				while ((line = br.readLine()) != null) {
					consoleLog << line
				}
				exit = process.waitFor()
			} catch (InterruptedException ignore) {
				return
			}
		}
	}

}
