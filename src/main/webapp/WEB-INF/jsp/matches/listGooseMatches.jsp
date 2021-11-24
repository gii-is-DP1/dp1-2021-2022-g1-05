<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="gooseMatches">
    <h2>Listing all ludo matches</h2>

    <table id="matchesTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Match code</th>
            <th style="width: 200px;">Start date</th>
            <th style="width: 200px;">End date</th>

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
            <td>
                <spring:url value="/gooseMatches/close/{matchId}" var="matchUrl">
                    <spring:param name="matchId" value="${match.id}"/>
                </spring:url>
                <a href="${fn:escapeXml(matchUrl)}">Close match</a>
            </td>

            </c:forEach>
        </tbody>
    </table>
</parchisYOca:layout>
