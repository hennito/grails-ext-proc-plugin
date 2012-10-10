<%@ page import="grails.plugin.extproc.ExternalProcess" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="extproc" />
        <g:set var="entityName" value="${message(code: 'externalProcess.label', default: 'ExternalProcess')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${externalProcessInstance}">
            <div class="errors">
                <g:renderErrors bean="${externalProcessInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="externalProcess.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${externalProcessInstance?.name}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="command"><g:message code="externalProcess.command.label" default="Command" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'command', 'errors')}">
                                    <g:textField name="command" value="${externalProcessInstance?.command}" />
                                </td>
                            </tr>
                   
                   			<tr class="prop" id="tokenPatternTr">
                                <td valign="top" class="name">
                                    <label for="tokenPattern"><g:message code="externalProcess.tokenPattern.label" default="Token Pattern" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'tokenPattern', 'errors')}">
                                    <g:textField name="tokenPattern" value="${externalProcessInstance?.tokenPattern}" />
                                </td>
                            </tr>
                            
                            
                            <tr>
                            <td></td>
                            <td>Note: ${ExternalProcess.WORKDIR_PLACEHOLDER} in default params or env will be replaced with the path of the workdir</td>
                            </tr>
                             <tr class="prop" id="defaultParamsTr">
                                <td valign="top" class="name">
                                    <label for="defaultParams"><g:message code="externalProcess.defaultParams.label" default="Default Params" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'defaultParams', 'errors')}">
                                	<ul id="defaultParamsList">
                                    <g:each in="${externalProcessInstance?.defaultParams}" var="aFile" status="idx">
                                    	<li><g:textField name="defaultParams[${idx}]" value="${aFile?:''}" /><a onclick="return removeMe(this)" href="#">x</a></li>
                                    </g:each>
                                    </ul>
                                    <a class="int-link" onclick="return addStrToList('defaultParams')" href="#">Add</a></li>
                                </td>
                                
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="env"><g:message code="externalProcess.env.label" default="Env" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'env', 'errors')}">
                                    <ul id="envMap">
                                    <g:each in="${externalProcessInstance?.env}" var="item" status="idx">
                                    	<li>
                                    		<g:textField class="mapKey" name="env.key[${idx}]" value="${item.key?:''}" /> 
                                    	   	<g:textField class="mapValue" name="env.value[${idx}]" value="${item.value?:''}" /> 
                                    	   	<a onclick="return removeMe(this)" href="#">x</a>
                                    	</li>
                                    </g:each>
                                    </ul>
                                    <a class="int-link" onclick="return addStrPairToList('env')" href="#">Add</a></li>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="workDir"><g:message code="externalProcess.workDir.label" default="Work Dir" /></label>
                                </td>
                                
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'workDir', 'errors')}">
                                	<g:select onchange="onWorkDirChange(this)" from="${['NEW','NONE','CUSTOM']}" name="wDir" /><br/>
                                    <g:textField onchange="onWorkDirChange(this)"  name="workDir" value="${externalProcessInstance?.workDir}" />
                                </td>
                            </tr>
                            
                            <tr class="prop workDir" id="requiredFilesTr">
                                <td valign="top" class="name">
                                    <label for="requiredFiles"><g:message code="externalProcess.requiredFiles.label" default="Required Files" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'requiredFiles', 'errors')}">
                                	<ul id="requiredFilesList">
                                    <g:each in="${externalProcessInstance?.requiredFiles}" var="aFile" status="idx">
                                    	<li><g:textField name="requiredFiles[${idx}]" value="${aFile?:''}" /><a onclick="return removeMe(this)" href="#">x</a></li>
                                    </g:each>
                                    </ul>
                                    <a class="int-link" onclick="return addStrToList('requiredFiles')" href="#">Add</a></li>
                                </td>
                                
                            </tr>
                            
                        
                        	<tr class="prop workDir" id="allowedFilesPatternTr">
                                <td valign="top" class="name">
                                    <label for="allowedFilesPattern"><g:message code="externalProcess.allowedFilesPattern.label" default="Allowed Files Pattern" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'allowedFilesPattern', 'errors')}">
                                    <g:textField name="allowedFilesPattern" value="${externalProcessInstance?.allowedFilesPattern}" />
                                </td>
                            </tr>

                            <tr class="prop workDir" id="allowedFilesTr">
                                <td valign="top" class="name">
                                    <label for="allowedFiles"><g:message code="externalProcess.allowedFiles.label" default="Allowed Files" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'allowedFiles', 'errors')}">
                                	<ul id="allowedFilesList">
                                    <g:each in="${externalProcessInstance?.allowedFiles}" var="aFile" status="idx">
                                    	<li><g:textField name="allowedFiles[${idx}]" value="${aFile?:''}" /> <a onclick="return removeMe(this)" href="#">x</a></li>
                                    </g:each>
                                    </ul>
                                    <a class="int-link" onclick="return addStrToList('allowedFiles')" href="#">Add</a></li>
                                </td>
                                
                            </tr>
                        
                            <tr class="prop workDir">
                                <td valign="top" class="name">
                                    <label for="returnZippedDir"><g:message code="externalProcess.returnZippedDir.label" default="Return Zipped Dir" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'returnZippedDir', 'errors')}">
                                    <g:checkBox onchange="onReturnDirChange(this)" name="returnZippedDir" value="${externalProcessInstance?.returnZippedDir}" />
                                </td>
                            </tr>
                        
                            <tr class="prop workDir returnDir">
                                <td valign="top" class="name">
                                    <label for="returnFilesPattern"><g:message code="externalProcess.returnFilesPattern.label" default="Return Files Pattern" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'returnFilesPattern', 'errors')}">
                                    <g:textField name="returnFilesPattern" value="${externalProcessInstance?.returnFilesPattern}" />
                                </td>
                            </tr>
                            
                        	<tr class="prop workDir returnDir">
                                <td valign="top" class="name">
                                    <label for="returnFiles"><g:message code="externalProcess.returnFiles.label" default="Return Files" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'returnFiles', 'errors')}">
                                	<ul id="returnFilesList">
                                    <g:each in="${externalProcessInstance?.returnFiles}" var="aFile" status="idx">
                                    	<li><g:textField name="returnFiles[${idx}]" value="${aFile?:''}" /><a onclick="return removeMe(this)" href="#">x</a></li>
                                    </g:each>
                                    </ul>
                                    <a class="int-link" onclick="return addStrToList('returnFiles')" href="#">Add</a></li>
                                </td>
                            </tr>
                        
                            <tr class="prop workDir">
                                <td valign="top" class="name">
                                    <label for="cleanUpWorkDir"><g:message code="externalProcess.cleanUpWorkDir.label" default="Clean Up Work Dir" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'cleanUpWorkDir', 'errors')}">
                                    <g:checkBox name="cleanUpWorkDir" value="${externalProcessInstance?.cleanUpWorkDir}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="exposedViaWS"><g:message code="externalProcess.exposedViaWS.label" default="Exposed Via WS" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'exposedViaWS', 'errors')}">
                                    <g:checkBox name="exposedViaWS" value="${externalProcessInstance?.exposedViaWS}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="isRemote"><g:message code="externalProcess.isRemote.label" default="Is Remote" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'isRemote', 'errors')}">
                                    <g:checkBox name="isRemote" value="${externalProcessInstance?.isRemote}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="timeout"><g:message code="externalProcess.timeout.label" default="Timeout" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: externalProcessInstance, field: 'timeout', 'errors')}">
                                    <g:textField name="timeout" value="${fieldValue(bean: externalProcessInstance, field: 'timeout')}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
