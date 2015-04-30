<%@ page import="grails.plugin.extproc.ExternalProcess" %>
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
            <g:form action="run" method="post" enctype="multipart/form-data">
                <table>
                    <tbody>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.name.label" default="Name" /></td>
                            <td valign="top" class="value"><g:link action="show" id="${externalProcessInstance?.id}">${fieldValue(bean: externalProcessInstance, field: "name")}</g:link></td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.User.label" default="User" /></td>
                            <td valign="top" class="value"><g:textField name="name" value="${fieldValue(bean: input, field: "user")}"/></td>
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="externalProcess.token.label" default="Token" /></td>
                            <td valign="top" class="value"><g:textField name="token" value="${fieldValue(bean: input, field: "token")}"/></td>
                        </tr>

						<tr class="prop" id="parametersTr">
                                <td valign="top" class="name">
                                    <label for="parameters"><g:message code="externalProcess.parameters.label" default="Parameters" /></label>
                                </td>
                                <td valign="top" class="value">
                                	<ul id="parametersList">
                                	<g:each in="${input?.parameters}" var="p" status="idx">
                                	<li><g:textField name="parameters[${idx}]" value="${p}"/> <a onclick="return removeMe(this)" href="#">x</a></li>
                                	</g:each>
                                    </ul>
                                    <a class="int-link" onclick="return extproc.addStrToList('parameters')" href="#">Add</a></li>
                                </td>

                            </tr>
<g:if test="${externalProcessInstance.workDir != ExternalProcess.NO_WORKDIR}">
<tr>
<td valign="top" class="name">zipped input files</td>
<td valign="top" class="value"><input type="file" name="zippedInput" /></td>
</tr>
<tr>
<td valign="top" class="name">Download results</td>
<td valign="top" class="name"><g:checkBox name="downloadZippedDir" value="${externalProcessInstance?.returnZippedDir}" /></td>
</tr>
</g:if>
                                       </tbody>
                </table>

            <div class="buttons">
                    <g:hiddenField name="id" value="${externalProcessInstance?.id}" />
                    <span class="button"><g:actionSubmit class="run" action="run" value="${message(code: 'default.button.run.label', default: 'Run')}" /></span>
            </div>
                </g:form>
            </div>

        </div>
    </body>
</html>
