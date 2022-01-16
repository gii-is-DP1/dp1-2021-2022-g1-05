<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="gooseMatches">
    <h2>Listing matches</h2>

    <table id="matchesTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Match code</th>
            <th style="width: 220px;">Start date</th>
            <th style="width: 220px;">End date</th>
            <th style="width:150px;">Winner</th>
            <th style="width:150px;">My stats</th>

        </tr>
        </thead>
        <tbody>
        <c:forEach items="${matches}" var="match">
        <tr>
            <td>
                <c:out value="${match.key.matchCode}"/>
            </td>
            <td>
                <c:out value="${match.key.startDate}"/>
            </td>
            <td>
                <c:out value="${match.key.endDate}"/>
            </td>
            <td>
                <c:out value="${match.value}"/>
            </td>
            <td>
                <spring:url value="/players/{playerId}/matchStats/{matchCode}" var="url">
                    <spring:param name="playerId" value="${playerId}"/>
                    <spring:param name="matchCode" value="${match.key.matchCode}"/>
                </spring:url>
                <a href="${fn:escapeXml(url)}">Stats</a>
            </td>

            </c:forEach>
        </tbody>
    </table>


    <c:choose>
        <c:when test="${gameFrom eq 'ludo'}">
            <div class="row" style="text-align: center">
                <p>Select page to display: </p>
                <c:forEach begin="0" end="${numberOfPages}" var="number">
                    <spring:url value="/players/{playerId}/ludoMatchesPlayed?page={number}" var="pageUrl">
                        <spring:param name="number" value="${number}"/>
                        <spring:param name="playerId" value="${playerId}"/>
                    </spring:url>
                    <a class="button button-primary" href="${fn:escapeXml(pageUrl)}">${number}</a>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row" style="text-align: center">
                <p>Select page to display: </p>
                <c:forEach begin="0" end="${numberOfPages}" var="number">
                    <spring:url value="/players/{playerId}/gooseMatchesPlayed?page={number}" var="pageUrl">
                        <spring:param name="number" value="${number}"/>
                        <spring:param name="playerId" value="${playerId}"/>
                    </spring:url>
                    <a class="button button-primary" href="${fn:escapeXml(pageUrl)}">${number}</a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</parchisYOca:layout>
