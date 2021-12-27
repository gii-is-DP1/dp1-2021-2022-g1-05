<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="gooseMatches">
    <h2>Listing all goose matches</h2>

    <table id="matchesTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Match code</th>
            <th style="width: 220px;">Start date</th>
            <th style="width: 220px;">End date</th>
            <th style="width:150px;">Status</th>
            <th>Actions</th>

        </tr>
        </thead>
        <tbody>
        <c:forEach items="${gooseMatches}" var="match">
        <tr>
            <td>
                <c:out value="${match.matchCode}"/>
            </td>
            <td>
                <c:out value="${match.startDate}"/>
            </td>
            <td>
                <c:out value="${match.endDate}"/>
            </td>
            <c:choose>
                <c:when test="${match.closedLobby == 1}">
                <td>Closed lobby</td>
                <td>
                    None
                </td>
                </c:when>
                <c:when test="${match.startDate == null}">
                    <td>Lobby</td>
                    <td>
                        None
                    </td>
                </c:when>
                <c:when test="${match.endDate == null}">
                    <td>
                        In progress
                    </td>
                    <td>
                        <spring:url value="/gooseMatches/close/{matchId}" var="matchUrl">
                            <spring:param name="matchId" value="${match.id}"/>
                        </spring:url>
                        <a href="${fn:escapeXml(matchUrl)}">Close match</a>
                    </td>
                </c:when>
                <c:otherwise>
                    <td>Finished</td>
                    <td>None</td>
                </c:otherwise>
            </c:choose>

            </c:forEach>
        </tbody>
    </table>
</parchisYOca:layout>
