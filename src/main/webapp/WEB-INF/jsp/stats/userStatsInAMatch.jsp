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
                    <tr>
                        <th>Times where you have landed on one of the geese squares</th>
                        <td><c:out value="${gooseStats.landedGeese}"/></td>
                    </tr>
                    <tr>
                        <th>Times where you have landed on one of the dice squares</th>
                        <td><c:out value="${gooseStats.landedDice}"/></td>
                    </tr>
                    <tr>
                        <th>Times where you have landed on one of the bridge squares</th>
                        <td><c:out value="${gooseStats.landedBridges}"/></td>
                    </tr>
                    <tr>
                        <th>Times where you have landed on the jail square</th>
                        <td><c:out value="${gooseStats.landedJails}"/></td>
                    </tr>
                    <tr>
                        <th>Times where you have landed on the inn square</th>
                        <td><c:out value="${gooseStats.landedInn}"/></td>
                    </tr>
                    <tr>
                        <th>Times where you have landed on the maze square</th>
                        <td><c:out value="${gooseStats.landedMaze}"/></td>
                    </tr>
                    <tr>
                        <th>Times where you have landed on the death square</th>
                        <td><c:out value="${gooseStats.landedDeath}"/></td>
                    </tr>
                    <tr>
                        <th>Number of double rolls</th>
                        <td><c:out value="${gooseStats.doubleRolls}"/></td>
                    </tr>
                </c:when>
            <c:otherwise>
                <tr>
                    <th>Number of tokens you ate</th>
                    <td><c:out value="${ludoStats.eatenTokens}"/></td>
                </tr>
                <tr>
                    <th>Number of double rolls</th>
                    <td><c:out value="${ludoStats.doubleRolls}"/></td>
                </tr>
                <tr>
                    <th>Number of greedy rolls</th>
                    <td><c:out value="${ludoStats.greedyRolls}"/></td>
                </tr>
                <tr>
                    <th>Number of tokens you scored</th>
                    <td><c:out value="${ludoStats.scoredTokens}"/></td>
                </tr>
                <tr>
                    <th>Number of take outs</th>
                    <td><c:out value="${ludoStats.takeOuts}"/></td>
                </tr>
                <tr>
                    <th>Number of squares walked</th>
                    <td><c:out value="${ludoStats.walkedSquares}"/></td>
                </tr>
            </c:otherwise>
        </c:choose>
    </table>


</ParchisYOca:layout>
