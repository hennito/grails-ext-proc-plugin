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
            <h1><g:message code="default.execute.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">

                <table>
                    <tbody>
                    </tbody>
                </table>

            <g:each in="${output.consoleLog }" var="line">
            ${line }<br/>
            </g:each>

            <div class="buttons">
                    <g:hiddenField name="id" value="${externalProcessInstance?.id}" />
                    <span class="button"><g:actionSubmit class="run" action="run" value="${message(code: 'default.button.run.label', default: 'Run')}" /></span>
            </div>

            </div>

        </div>
    </body>
</html>
