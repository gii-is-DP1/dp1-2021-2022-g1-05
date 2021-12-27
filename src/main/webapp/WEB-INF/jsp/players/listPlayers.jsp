<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="players">
    <h2>Showing all players</h2>

    <table id="playersTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Name</th>
            <th style="width: 200px;">Email</th>
            <th style="width: 150px;">Enabled</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${players}" var="player">
            <tr>
                <td>
                    <spring:url value="/players/{playerId}" var="playerUrl">
                        <spring:param name="playerId" value="${player.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(playerUrl)}"><c:out value="${player.user.username}"/></a>
                </td>
                <td>
                    <c:out value="${player.email}"/>
                </td>
                <td>
                    <c:out value="${player.user.enabled}"/>
                </td>
                <td>
                    <c:if test="${player.user.enabled eq true}">
                        <spring:url value="/players/disable/{playerId}" var="playerUrl">
                            <spring:param name="playerId" value="${player.id}"/>
                        </spring:url>
                        <a href="${fn:escapeXml(playerUrl)}">Disable</a>
                    </c:if>
                    <c:if test="${player.user.enabled eq false}">
                        <spring:url value="/players/enable/{playerId}" var="playerUrl">
                            <spring:param name="playerId" value="${player.id}"/>
                        </spring:url>
                        <a href="${fn:escapeXml(playerUrl)}">Enable</a>
                    </c:if>
                    |
                    <spring:url value="/players/{playerId}/delete" var="playerUrl">
                        <spring:param name="playerId" value="${player.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(playerUrl)}">Delete</a>
                </td>

            </tr>
        </c:forEach>
        </tbody>
    </table>
</ParchisYOca:layout>
