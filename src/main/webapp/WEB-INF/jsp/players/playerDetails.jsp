<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="players">

    <h2>Player Information</h2>

    <table class="table table-striped">
        <tr>
            <th>Email</th>
            <td><b><c:out value="${player.email}"/></b></td>
        </tr>
        <tr>
            <th>Username</th>
            <td><c:out value="${player.username}"/></td>
        </tr>
    </table>

    <spring:url value="{playerId}/edit" var="editUrl">
        <spring:param name="playerId" value="${player.id}"/>
    </spring:url>
    <a href="${fn:escapeXml(editUrl)}" class="btn btn-default">Edit Player</a>


</parchisYOca:layout>
