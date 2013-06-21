<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="extproc" />
        <g:set var="entityName" value="${message(code: 'externalProcess.label', default: 'ExternalProcess')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.id.label" default="Id" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "id")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.name.label" default="Name" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "name")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.command.label" default="Command" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "command")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.workDir.label" default="Work Dir" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "workDir")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.env.label" default="Env" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "env")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.defaultParams.label" default="Default Params" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "defaultParams")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.allowedFilesPattern.label" default="Allowed Files Pattern" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "allowedFilesPattern")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.allowedFiles.label" default="Allowed Files" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "allowedFiles")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.requiredFiles.label" default="Required Files" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "requiredFiles")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.returnFilesPattern.label" default="Return Files Pattern" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "returnFilesPattern")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.returnFiles.label" default="Return Files" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "returnFiles")}</td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.cleanUpWorkDir.label" default="Clean Up Work Dir" /></td>
                            <td valign="top" class="value"><g:formatBoolean boolean="${externalProcessInstance?.cleanUpWorkDir}" /></td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.exposedViaWS.label" default="Exposed Via WS" /></td>
                            <td valign="top" class="value"><g:formatBoolean boolean="${externalProcessInstance?.exposedViaWS}" /></td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.isRemote.label" default="Is Remote" /></td>
                            <td valign="top" class="value"><g:formatBoolean boolean="${externalProcessInstance?.isRemote}" /></td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.returnZippedDir.label" default="Return Zipped Dir" /></td>
                            <td valign="top" class="value"><g:formatBoolean boolean="${externalProcessInstance?.returnZippedDir}" /></td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.timeout.label" default="Timeout" /></td>
                            <td valign="top" class="value">${fieldValue(bean: externalProcessInstance, field: "timeout")}</td>
                        </tr>

                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${externalProcessInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
