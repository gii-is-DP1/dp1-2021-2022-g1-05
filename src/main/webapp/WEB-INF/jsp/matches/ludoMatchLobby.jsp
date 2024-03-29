<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="createLudoMatch">
    <h1>Ludo match lobby</h1>
    <h2>Match code: <h2 style="color: #5390D9">${matchCode}</h2></h2>

    <br>

    <h3>Joined players:</h3>
    <ol>
        <c:forEach items="${stats}" var="stat">
                    <li><p><c:out value="${stat.player.user.username}"/></p></li>
        </c:forEach>
    </ol>

    <c:if test="${isOwner eq 1 && numberOfPlayers > 1}">
    <a href="/ludoMatches/${matchId}"><button class="btn btn-default" type="submit">Start game</button></a>
    </c:if>
    <a href="/ludoMatches/matchLeft"><button class="btn btn-danger" type="submit">Leave the game</button></a>

</ParchisYOca:layout>
