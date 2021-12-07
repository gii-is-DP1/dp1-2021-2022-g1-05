<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="gooseMatch">
    <h2>Goose game match!</h2>

    <h3>Joined players:</h3>
    <ol>
        <c:forEach items="${stats}" var="stat">
            <li><p><c:out value="${stat.player.user.username}"/></p></li>
        </c:forEach>
    </ol>

</ParchisYOca:layout>
