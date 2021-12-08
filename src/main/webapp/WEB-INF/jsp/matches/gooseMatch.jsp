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
        <div class="col-md-2">
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
            <c:if test="${hasTurn eq 1}">
                <p style="color: #5390D9">Is your turn!</p>
                <a href="/session/rolldices"><button class="btn btn-default">Roll the dice</button></a>
            </c:if>

            <br>
            <br>
            <c:if test="${firstDice eq secondDice}">
                <p style="color: #5390D9">You got a double roll!!!!!!</p>
            </c:if>
            <p><c:out value="The first dice: ${firstDice}"/></p>
            <p><c:out value="The second dice: ${secondDice}"/></p>
            <p><c:out value="The sum of both: ${sumDice}"/></p>
        </div>
        <!-- TODO SUPER PROVISIONAL -->
        <div class="col-md-2">
            <h3>Displaying static board:</h3>
            <img STYLE="width: auto; height: 300px" src="<spring:url value="/resources/images/provisional-goose-board.png" htmlEscape="true" />">
        </div>
        </div>
    </div>


</ParchisYOca:layout>
