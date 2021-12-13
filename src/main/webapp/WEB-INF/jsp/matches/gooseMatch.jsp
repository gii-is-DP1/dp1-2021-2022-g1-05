<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="gooseMatch">
    <h2>Goose game match!</h2>

    <div class="container">
        <div class="row">
        <div class="col-md-4">
        <h3>Joined players:</h3>
        <ul>
            <c:forEach items="${stats}" var="stat">
                <c:forEach items="${chips}" var="chip">
                    <c:if test="${stat.inGameId eq chip.inGameId}">
                        <li><p><c:out value="${stat.player.user.username}: ${chip.position}"/></p></li>
                    </c:if>
                </c:forEach>
            </c:forEach>
        </ul>
            <br>
            <c:choose>
                <c:when test="${hasTurn eq 1}">
                    <p style="color: #5390D9">Is your turn!</p>
                    <a href="/session/rolldices"><button class="btn btn-default">Roll the dice</button></a>
                </c:when>
                <c:otherwise>
                    <p style="color: darkred">You have to wait until its your turn!</p>
                </c:otherwise>
            </c:choose>

            <br>
            <br>
            <p><c:out value="The first dice: ${firstDice}"/></p>
            <p><c:out value="The second dice: ${secondDice}"/></p>
            <p><c:out value="The sum of both: ${sumDice}"/></p>
        </div>
        <!-- TODO SUPER PROVISIONAL -->
        <div class="col-md-2">
            <ParchisYOca:gooseBoard gooseBoard="${gooseBoard}"/>
            <c:forEach items="${gooseBoard.chips}" var="chip">
            	<ParchisYOca:gooseChip size="10" inGameId="${chip}" position="${chip.getPosition()}"/>
            </c:forEach> 
        </div>
        </div>
    </div>


</ParchisYOca:layout>
