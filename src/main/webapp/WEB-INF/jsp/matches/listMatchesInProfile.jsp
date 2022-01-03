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

            </c:forEach>
        </tbody>
    </table>
</parchisYOca:layout>
