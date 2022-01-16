<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<parchisYOca:layout pageName="achievements">
    <h2>Achievements</h2>
    <c:if test="${isAdmin eq true}">

    <spring:url value="achievements/newAchievement/goose" var="newUrlGoose">
    </spring:url>
    <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(newUrlGoose)}" class="btn btn-default">New goose achievement</a>

    <spring:url value="achievements/newAchievement/ludo" var="newUrlLudo">
    </spring:url>
    <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(newUrlLudo)}" class="btn btn-default">New ludo achievement</a>
    </c:if>

    <br>
    <br>

    <table id="achievementsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Name</th>
            <th style="width: 200px;">Description</th>
            <th style="width: 200px;">Number to beat</th>
            <th style="width: 200px;">Is unlocked</th>
            <c:if test="${isAdmin eq true}">
            <th>Actions</th>
            </c:if>

        </tr>
        </thead>
        <tbody>
        <c:forEach items="${achievements}" var="achievement">
        <tr>
            <td>
                <c:out value="${achievement.name}"/>
            </td>
            <td>
                <c:out value="${achievement.description}"/>
            </td>
            <td>
                <c:out value="${achievement.numberToBeat}"/>
            </td>
            <td>
                <c:choose>
                    <c:when test="${fn:contains(unlockedAchievements, achievement)}">
                        <p>True</p>
                    </c:when>
                    <c:otherwise>
                        <p>False</p>
                    </c:otherwise>
                </c:choose>
            </td>
            <c:if test="${isAdmin eq true}">
            <td>
                <spring:url value="/achievements/delete/{achievementId}" var="achievementUrl">
                    <spring:param name="achievementId" value="${achievement.id}"/>
                </spring:url>
                <a href="${fn:escapeXml(achievementUrl)}">Delete</a>
            </td>
            </c:if>
        </tr>

            </c:forEach>
        </tbody>
    </table>
</parchisYOca:layout>
