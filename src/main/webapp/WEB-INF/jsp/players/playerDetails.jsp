<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="players">



    <c:choose>
        <c:when test="${hasPermission eq 'true'}">
            <h2>Player profile</h2>
            <spring:url value="{playerId}/edit" var="editUrl">
                <spring:param name="playerId" value="${player.id}"/>
            </spring:url>
            <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-default">Edit Player</a>
            <c:if test="${inGame != 'true'}">
                <spring:url value="{playerId}/delete" var="editUrl">
                    <spring:param name="playerId" value="${player.id}"/>
                </spring:url>
                <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-danger">Delete Player</a>
            </c:if>
            <br>
            <br>


            <br>

            <h3>Player credentials</h3>

            <table class="table table-striped">
                <thead>
                    <th style="width: 150px;">Email</th>
                    <th style="width: 200px;">Username</th>
                </thead>
                <tbody>
                    <td>
                        <c:out value="${player.email}"/>
                    </td>
                    <td>
                        <c:out value="${user.username}"/>
                    </td>
                </tbody>
            </table>

            <br>

            <h3>Player achievements unlocked</h3>

            <table id="achievementsPlayerTable" class="table table-striped">
                <thead>
                <tr>
                    <th style="width: 150px;">Achievement name</th>
                    <th style="width: 200px;">Achievement description</th>
                    <th style="width: 200px;">Achievement number to beat</th>

                </tr>
                </thead>
                <tbody>
                <c:forEach items="${player.achievements}" var="achievement">
                <tr>
                    <td>
                        <c:out value="${achievement.name}"/>
                    </td>
                    <td>
                        <c:out value="${achievement.description}"/>
                    </td>
                    <td>
                        <c:out value="${achievement.numberToBeat}"/>
                    </td>

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

            <h3>Played matches</h3>
            <h4>Number of total goose games played by you: <c:out value="${numberOfGooseGames}"></c:out></h4>
            <h4>Number of total ludo games played by you: <c:out value="${numberOfLudoGames}"></c:out></h4>
            <h4>Number of total games played by you: <c:out value="${numberOfGooseGames + numberOfLudoGames}"></c:out></h4>
            <h4>Average duration of the goose games played by you: <c:out value="${averageGooseDuration}"></c:out></h4>
            <h4>Average duration of the ludo games by you: <c:out value="${averageLudoDuration}"></c:out></h4>

            <br>

            <spring:url value="{playerId}/ludoMatchesPlayed?page=0" var="editUrl">
                <spring:param name="playerId" value="${player.id}"/>
            </spring:url>
            <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-default">Ludo matches</a>
            <spring:url value="{playerId}/gooseMatchesPlayed?page=0" var="editUrl">
                <spring:param name="playerId" value="${player.id}"/>
            </spring:url>
            <a style="display: inline-block; margin: 0;" href="${fn:escapeXml(editUrl)}" class="btn btn-default">Goose matches</a>
        </c:when>
        <c:otherwise>
            <h1>You dont have permission to visualize this data</h1>
        </c:otherwise>
    </c:choose>



</parchisYOca:layout>
