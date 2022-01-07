<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="th" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<ParchisYOca:layout pageName="players">
    <h2>Search player by username</h2>

    <form:form class="form-horizontal" id="player-id-form">
        <div class="form-group has-feedback" style="margin-left: 2%">
            <label for="Username" style="margin-right: 0.5%">Username: (leave blank to not filter)</label>
            <input type="text" name="Username" id="Username" placeholder="Enter a userName ">
        </div>
        <div class="form-group">
            <div class="col-sm col-sm-10" style="margin-left: 2%">
                <button class="btn btn-default" type="submit">Search</button>
            </div>
        </div>
    </form:form>

    <br>

    <h2>Showing players</h2>

    <table id="playersTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Name</th>
            <th style="width: 200px;">Email</th>
            <th style="width: 150px;">Enabled</th>
            <th style="width: 150px;">In game</th>
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
                    <c:choose>
                        <c:when test="${fn:contains(playersInGame, player.user.username)}">
                            <p>true</p>
                        </c:when>
                        <c:otherwise>
                            false
                        </c:otherwise>
                    </c:choose>
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
                    <c:choose>
                        <c:when test="${fn:contains(playersInGame, player.user.username)}">
                            <p style="color: darkred; display: inline-block">Can't delete this account because the player is in game</p>
                        </c:when>
                        <c:otherwise>
                            <spring:url value="/players/{playerId}/delete" var="playerUrl">
                                <spring:param name="playerId" value="${player.id}"/>
                            </spring:url>
                            <a href="${fn:escapeXml(playerUrl)}">Delete</a>
                        </c:otherwise>
                    </c:choose>
                </td>


            </tr>
        </c:forEach>
        </tbody>
    </table>
</ParchisYOca:layout>
