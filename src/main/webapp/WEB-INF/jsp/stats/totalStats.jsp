<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="gooseMatch">

    <h3>Global stats</h3>
    <h4>Number of total goose games: <c:out value="${numberOfGooseGames}"></c:out></h4>
    <h4>Number of total ludo games: <c:out value="${numberOfLudoGames}"></c:out></h4>
    <h4>Number of total games: <c:out value="${numberOfLudoGames + numberOfGooseGames}"></c:out></h4>
    <h4>Average duration of goose games: <c:out value="${averageGooseDuration}"></c:out></h4>
    <h4>Average duration of ludo games: <c:out value="${averageLudoDuration}"></c:out></h4>

    <br>

    <h3>Ranking by ludo games won</h3>
    <table id="statsTable" class="table table-striped">
        <thead>
        <th>Player</th>
        <th>Wins</th>
        </thead>
        <tbody>
        <c:forEach items="${top3LudoWins}" var="top3LudoWins">
            <tr>
                <td><c:out value="${top3LudoWins.player.user.username}"/></td>
                <td><c:out value="${top3LudoWins.hasWon}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <br>

    <h3>Ranking by goose games won</h3>
    <table id="statsTable" class="table table-striped">
        <thead>
            <th>Player</th>
            <th>Wins</th>
        </thead>
        <tbody>
            <c:forEach items="${top3GooseWins}" var="top3GooseWins">
                <tr>
                    <td><c:out value="${top3GooseWins.player.user.username}"/></td>
                    <td><c:out value="${top3GooseWins.hasWon}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <br>

    <h3>Ranking by token eatens in ludo</h3>
    <table id="statsTable" class="table table-striped">
        <thead>
        <th>Player</th>
        <th>Ludo eaten tokens</th>
        </thead>
        <tbody>
        <c:forEach items="${top3EatenTokens}" var="top3EatenTokens">
            <tr>
                <td><c:out value="${top3EatenTokens.player.user.username}"/></td>
                <td><c:out value="${top3EatenTokens.eatenTokens}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <br>

    <h3>Ranking by times landed on goose squares</h3>
    <table id="statsTable" class="table table-striped">
        <thead>
        <th>Player</th>
        <th>Goose squares landed on</th>
        </thead>
        <tbody>
        <c:forEach items="${top3GooseSquares}" var="top3GooseSquares">
            <tr>
                <td><c:out value="${top3GooseSquares.player.user.username}"/></td>
                <td><c:out value="${top3GooseSquares.landedGeese}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <br>

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
            <th>Number of goose games won</th>
            <td><c:out value="${gooseStats.hasWon}"/></td>
            <th>Number of ludo games won</th>
            <td><c:out value="${ludoStats.hasWon}"/></td>
        </tr>
        <tr>
            <th>Number of double rolls</th>
            <td><c:out value="${gooseStats.doubleRolls}"/></td>
            <th>Number of double rolls</th>
            <td><c:out value="${ludoStats.doubleRolls}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on one of the geese squares</th>
            <td><c:out value="${gooseStats.landedGeese}"/></td>
            <th>Number of greedy rolls</th>
            <td><c:out value="${ludoStats.greedyRolls}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on the jail square</th>
            <td><c:out value="${gooseStats.landedJails}"/></td>
            <th>Number of tokens players ate</th>
            <td><c:out value="${ludoStats.eatenTokens}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on the inn square</th>
            <td><c:out value="${gooseStats.landedInn}"/></td>
            <th>Number of take outs</th>
            <td><c:out value="${ludoStats.takeOuts}"/></td>
        </tr>
        <tr>
            <th>Times where you players landed on the maze square</th>
            <td><c:out value="${gooseStats.landedMaze}"/></td>
            <th>Number of squares walked</th>
            <td><c:out value="${ludoStats.walkedSquares}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on the death square</th>
            <td><c:out value="${gooseStats.landedDeath}"/></td>
            <th>Number of tokens players scored</th>
            <td><c:out value="${ludoStats.scoredTokens}"/></td>
        </tr>
        <tr>
            <th>Times where players have landed on one of the dice squares</th>
            <td><c:out value="${gooseStats.landedDice}"/></td>
            <th></th>
            <td></td>
        </tr>
        <tr>
            <th>Times where players landed on one of the bridge squares</th>
            <td><c:out value="${gooseStats.landedBridges}"/></td>
            <th style="background-color: lightblue">Total number of games won</th>
            <td style="background-color: lightblue"><c:out value="${gooseStats.hasWon + ludoStats.hasWon}"/></td>
        </tr>
        </tbody>
    </table>

    <br>




</ParchisYOca:layout>
