<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<parchisYOca:layout pageName="ludoMatches">

    <h2>Filter ludo matches</h2>
    <p>It will show the matches starting from the specified date</p>
    <br>
    <form:form class="form-horizontal" id="date-filter-goose-form">
        <div class="form-group has-feedback" style="margin-left: 2%">
            <label for="filterBy">Filter by:</label>
            <select name="filterBy" id="filterBy">
                <option value="startDate">Start date</option>
                <option value="endDate">End date</option>
            </select>
            <label for="date" style="margin-left: 0.5%">Select date:</label>
            <input type="date" name="date" id="date">
        </div>
        <div class="form-group">
            <div class="col-sm col-sm-10" style="margin-left: 2%">
                <button class="btn btn-default" type="submit">Search</button>
            </div>
        </div>
    </form:form>
    <h2>Listing ludo matches</h2>

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
        <c:forEach items="${ludoMatches}" var="match">
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
                <!--Aquí se puede añadir que el admin cierre lobby pero xD -->
            </td>
            </c:when>
            <c:when test="${match.endDate == null}">
            <td>
                In progress
            </td>
            <td>
                <spring:url value="/ludoMatches/close/{matchId}" var="matchUrl">
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
