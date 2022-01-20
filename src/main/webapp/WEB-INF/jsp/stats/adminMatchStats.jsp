<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="gooseMatch">
    <h2>In game stats</h2>

    <table id="statsTable" class="table table-striped">
        <c:choose>
            <c:when test="${gooseStats ne null}">
                <thead>
                <th>Username</th>
                <th>Landed geese</th>
                <th>Landed dice</th>
                <th>Landed bridges</th>
                <th>Landed jails</th>
                <th>Landed inn</th>
                <th>Landed maze</th>
                <th>Landed death</th>
                <th>Double rolls</th>
                <th>Is winner</th>
                <th>In game id</th>
                </thead>
                <c:forEach items="${gooseStats}" var="stats">
                    <tbody>
                        <td><c:out value="${stats.player.user.username}"/></td>

                        <td><c:out value="${stats.landedGeese}"/></td>

                        <td><c:out value="${stats.landedDice}"/></td>

                        <td><c:out value="${stats.landedBridges}"/></td>

                        <td><c:out value="${stats.landedJails}"/></td>

                        <td><c:out value="${stats.landedInn}"/></td>

                        <td><c:out value="${stats.landedMaze}"/></td>

                        <td><c:out value="${stats.landedDeath}"/></td>

                        <td><c:out value="${stats.doubleRolls}"/></td>

                        <td><c:out value="${stats.hasWon}"/></td>

                        <td><c:out value="${stats.inGameId}"/></td>
                    </tbody>
                    </c:forEach>
                </c:when>
            <c:otherwise>
                <thead>
                <th>Username</th>
                <th>Eaten tokens</th>
                <th>Double rolls</th>
                <th>Greedy rolls</th>
                <th>Scored tokens</th>
                <th>Take outs</th>
                <th>Walked squares</th>
                <th>Is winner</th>
                <th>In game id</th>
                </thead>
                <c:forEach items="${ludoStats}" var="stats">
                    <tbody>
                        <td><c:out value="${stats.player.user.username}"/></td>

                        <td><c:out value="${stats.eatenTokens}"/></td>

                        <td><c:out value="${stats.doubleRolls}"/></td>

                        <td><c:out value="${stats.greedyRolls}"/></td>

                        <td><c:out value="${stats.scoredTokens}"/></td>

                        <td><c:out value="${stats.takeOuts}"/></td>

                        <td><c:out value="${stats.walkedSquares}"/></td>

                        <td><c:out value="${stats.hasWon}"/></td>

                        <td><c:out value="${stats.inGameId}"/></td>
                    </tbody>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </table>


</ParchisYOca:layout>
