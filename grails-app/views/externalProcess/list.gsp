<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="extproc" />
        <g:set var="entityName" value="${message(code: 'externalProcess.label', default: 'ExternalProcess')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <g:sortableColumn property="id" title="${message(code: 'externalProcess.id.label', default: 'Id')}" />
                            <g:sortableColumn property="name" title="${message(code: 'externalProcess.name.label', default: 'Name')}" />
                            <g:sortableColumn property="command" title="${message(code: 'externalProcess.command.label', default: 'Command')}" />
                            <g:sortableColumn property="workDir" title="${message(code: 'externalProcess.workDir.label', default: 'Work Dir')}" />
                            <g:sortableColumn property="env" title="${message(code: 'externalProcess.env.label', default: 'Env')}" />
                            <g:sortableColumn property="allowedFilesPattern" title="${message(code: 'externalProcess.allowedFilesPattern.label', default: 'Allowed Files Pattern')}" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${externalProcessInstanceList}" status="i" var="externalProcessInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:link action="show" id="${externalProcessInstance.id}">${fieldValue(bean: externalProcessInstance, field: "id")}</g:link></td>
                            <td><g:link action="execute" id="${externalProcessInstance.id}">${fieldValue(bean: externalProcessInstance, field: "name")}</g:link></td>
                            <td>${fieldValue(bean: externalProcessInstance, field: "command")}</td>
                            <td>${fieldValue(bean: externalProcessInstance, field: "workDir")}</td>
                            <td>${fieldValue(bean: externalProcessInstance, field: "env")}</td>
                            <td>${fieldValue(bean: externalProcessInstance, field: "allowedFilesPattern")}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${externalProcessInstanceCount}" />
            </div>
        </div>
    </body>
</html>
