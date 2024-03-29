<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>
<%@ page import="org.springframework.samples.parchisYOca.ludoChip.GameState" %>

<ParchisYOca:layout pageName="ludoMatch">
    <h2>Ludo game match!</h2>
    <div class="container">
        <div class="row">
            <div class="col-md-3">
                <h3>Chips list:</h3>
                <ul>
                    <c:forEach items="${stats}" var="stat">
                        <c:forEach items="${chips}" var="chip">
                            <c:choose>
                                <c:when test="${stat.inGameId eq chip.inGamePlayerId and stat.playerLeft eq 0}">
                                    <li><p>
                                        <c:choose>
                                            <c:when test="${chip.position eq null}">
                                                <c:out value="${chip.getColor()}-> ${stat.player.user.username}: At home"/>
                                            </c:when>
                                            <c:when test="${chip.gameState eq GameState.endGame and chip.position eq 7}">
                                                <c:out value="${chip.getColor()}-> ${stat.player.user.username}: FINAL TILE"/>
                                            </c:when>
                                            <c:when test="${diceIndex ne null and thisPlayerStats.inGameId eq chip.inGamePlayerId}">
                                                <c:choose>
                                                    <c:when test="${fn:contains(chipsToMove, chip.inGameChipId)}">
                                                        <spring:url value="/ludoInGame/sumDice/{diceIndex}/{inGameChipId}" var="sumDiceURL">
                                                            <spring:param name="diceIndex" value="${diceIndex}"/>
                                                            <spring:param name="inGameChipId" value="${chip.inGameChipId}"/>
                                                        </spring:url>
                                                        <a href="${fn:escapeXml(sumDiceURL)}">
                                                            <c:choose>
                                                                <c:when test="${chip.gameState eq GameState.endGame}">
                                                                    <c:out value="${chip.getColor()}-> ${stat.player.user.username}: Last stage ${chip.position+1}"/>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:out value="${chip.getColor()}-> ${stat.player.user.username}: ${chip.position+1}"/>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${chip.getColor()}-> ${stat.player.user.username}: ${chip.position+1}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${chip.gameState eq GameState.endGame}">
                                                        <c:out value="${chip.getColor()}-> ${stat.player.user.username}: Last stage ${chip.position+1}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${chip.getColor()}-> ${stat.player.user.username}: ${chip.position+1}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                    </p></li>
                                </c:when>
                                <c:when test="${stat.inGameId eq chip.inGamePlayerId and stat.playerLeft eq 1}">
                                    <li><p><c:out value="${chip.getColor()}-> JUGADOR INACTIVO: ${chip.position+1}"/></p></li>
                                </c:when>
                            </c:choose>


                        </c:forEach>
                    </c:forEach>
                </ul>
            </div>
            <div class="col-md-2">
                <c:if test="${hasTurn ne null}">
                    <c:choose>
                        <c:when test="${hasTurn eq 1}">
                            <p style="color: #5390D9">It's your turn!</p>
                            <a href="/session/rolldices"><button class="btn btn-default">Roll the dice</button></a>
                        </c:when>
                        <c:otherwise>
                            <p style="color: darkred">You have to wait until it's your turn!</p>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
            <div class="col-md-2">
                <p><c:out value="First dice: ${firstDice}"/></p>
                <p><c:out value="Second dice: ${secondDice}"/></p>
                <p><c:out value="The sum of both: ${sumDice}"/></p>
            </div>

            <div class="col-md-3">
                <c:if test="${dicesRolled ne null}">
                    <p><c:out value="Which dice will you sum first?"/></p>
                    <spring:url value="/ludoInGame/chooseChip/0" var="chooseChipURL1"></spring:url>
                    <spring:url value="/ludoInGame/chooseChip/1" var="chooseChipURL2"></spring:url>
                    <c:choose>
                        <c:when test="${diceCode eq 0}">
                            <img src="../resources/images/dado1.png" width="100px" style="opacity: 0.4">
                            <a href="${fn:escapeXml(chooseChipURL2)}">
                                <img src="../resources/images/dado2.png" width="100px">
                            </a>
                        </c:when>
                        <c:when test="${diceCode eq 1}">
                            <a href="${fn:escapeXml(chooseChipURL1)}">
                                <img src="../resources/images/dado1.png" width="100px">
                            </a>
                            <img src="../resources/images/dado2.png" width="100px" style="opacity: 0.4">
                        </c:when>
                        <c:otherwise>
                            <a href="${fn:escapeXml(chooseChipURL1)}">
                                <img src="../resources/images/dado1.png" width="100px">
                            </a>
                            <a href="${fn:escapeXml(chooseChipURL2)}">
                                <img src="../resources/images/dado2.png" width="100px">
                            </a>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>


            <c:if test="${hasEnded != 1}">
                <a href="/ludoMatches/matchLeft"><button class="btn btn-danger" type="submit">Leave the game</button></a>
            </c:if>


            <div class="tablero">
                <ParchisYOca:ludoBoard ludoBoard="${ludoBoard}"/>
                <c:forEach items="${chips}" var="chip">
                    <ParchisYOca:ludoChip size="40" chip="${chip}" position="${chip.position}" chipsToBeDisplaced="${chipsToBeDisplaced}"/>
                </c:forEach>

            </div>
        </div>
    </div>

</ParchisYOca:layout>
