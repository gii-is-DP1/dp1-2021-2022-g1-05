<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="players">



    <c:choose>
        <c:when test="${hasPermission eq 'true'}">
            <h2>Player profile</h2>
            <spring:url value="{playerId}/edit" var="editUrl">
                <spring:param name="playerId" value="${player.id}"/>
            </spring:url>
            <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-default">Edit Player</a>
            <c:if test="${inGame != 'true'}">
                <spring:url value="{playerId}/delete" var="editUrl">
                    <spring:param name="playerId" value="${player.id}"/>
                </spring:url>
                <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-danger">Delete Player</a>
            </c:if>
            <br>
            <br>


            <br>

            <h3>Player credentials</h3>

            <table class="table table-striped">
                <tr>
                    <th>Email</th>
                    <td><c:out value="${player.email}"/></td>
                </tr>
                <tr>
                    <th>Username</th>
                    <td><c:out value="${user.username}"/></td>
                </tr>
            </table>

            <br>

            <h3>Player achievements</h3>

            <table id="achievementsPlayerTable" class="table table-striped">
                <thead>
                <tr>
                    <th style="width: 150px;">Achievement name</th>
                    <th style="width: 200px;">Achievement description</th>

                </tr>
                </thead>
                <tbody>
                <c:forEach items="${player.achievements}" var="achievement">
                <tr>
                    <td>
                        <c:out value="${achievement.name}"/>
                    </td>
                    <td>
                        <c:out value="${achievement.description}"/>
                    </td>

                    </c:forEach>
                </tbody>
            </table>

            <h3>Played matches</h3>

            <spring:url value="{playerId}/ludoMatchesPlayed" var="editUrl">
                <spring:param name="playerId" value="${player.id}"/>
            </spring:url>
            <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-default">Ludo matches</a>
            <spring:url value="{playerId}/gooseMatchesPlayed" var="editUrl">
                <spring:param name="playerId" value="${player.id}"/>
            </spring:url>
            <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-default">Goose matches</a>
        </c:when>
        <c:otherwise>
            <h1>You dont have permission to visualize this data</h1>
        </c:otherwise>
    </c:choose>



</parchisYOca:layout>
