<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="gooseMatch">
    <h3>Played stats</h3>
    <table id="statsPlayerTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Goose stats</th>
            <th style="width: 150px;"></th>
            <th style="width: 200px;">Ludo stats</th>
            <th style="width: 150px;"></th>

        </tr>
        </thead>
        <tbody>
        <tr>
            <th>Times where players have landed on one of the geese squares</th>
            <td><c:out value="${gooseStats.landedGeese}"/></td>
            <th>Number of tokens players ate</th>
            <td><c:out value="${ludoStats.eatenTokens}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on one of the dice squares</th>
            <td><c:out value="${gooseStats.landedDice}"/></td>
            <th>Number of double rolls</th>
            <td><c:out value="${ludoStats.doubleRolls}"/></td>
        </tr>
        <tr>
            <th>Times where players landed on one of the bridge squares</th>
            <td><c:out value="${gooseStats.landedBridges}"/></td>
            <th>Number of greedy rolls</th>
            <td><c:out value="${ludoStats.greedyRolls}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on the jail square</th>
            <td><c:out value="${gooseStats.landedJails}"/></td>
            <th>Numbers of blocks players created</th>
            <td><c:out value="${ludoStats.createdBlocks}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on the inn square</th>
            <td><c:out value="${gooseStats.landedInn}"/></td>
            <th>Number of tokens players scored</th>
            <td><c:out value="${ludoStats.scoredTokens}"/></td>
        </tr>
        <tr>
            <th>Times where you players landed on the maze square</th>
            <td><c:out value="${gooseStats.landedMaze}"/></td>
            <th>Number of take outs</th>
            <td><c:out value="${ludoStats.takeOuts}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on the death square</th>
            <td><c:out value="${gooseStats.landedDeath}"/></td>
            <th>Number of squares walked</th>
            <td><c:out value="${ludoStats.walkedSquares}"/></td>
        </tr>
        <tr>
            <th>Number of double rolls</th>
            <td><c:out value="${gooseStats.doubleRolls}"/></td>
            <th>Number of ludo games won</th>
            <td><c:out value="${ludoStats.hasWon}"/></td>
        </tr>
        <tr>
            <th>Number of goose games won</th>
            <td><c:out value="${gooseStats.hasWon}"/></td>
            <th style="background-color: lightblue">Total number of games won</th>
            <td style="background-color: lightblue"><c:out value="${gooseStats.hasWon + ludoStats.hasWon}"/></td>
        </tr>
        </tbody>
    </table>

    <br>


</ParchisYOca:layout>
